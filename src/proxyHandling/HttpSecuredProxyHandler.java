package proxyHandling;

import auth.JwtFilter;
import auth.UserDetails;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import util.Pair;

import java.net.URL;
import java.nio.file.AccessDeniedException;
import java.util.*;

@AllArgsConstructor
public class HttpSecuredProxyHandler implements HttpHandler {
    private Map<String, Pair<String, List<String>>> securedRoutes;
    private JwtFilter jwtFilter;

    @SneakyThrows
    @Override
    public void handle(HttpExchange exchange) {
        String route = String.valueOf(exchange.getRequestURI());
        String proxyRoute = securedRoutes.get(route).getFirst();
        URL proxyUrl = new URL(proxyRoute);
        try {
            try {
                UserDetails userDetails = jwtFilter.validateTokenAndExtractUserInfo(exchange);
                if(userDetails.isBlocked()){
                    throw new AccessDeniedException("Access denied, this user is blocked");
                }
                List<String> rolesForAccess = securedRoutes.get(route).getSecond();
                if(rolesForAccess.size() != 0){
                    if(!Contains(rolesForAccess, userDetails.getRoles())){
                        throw new AccessDeniedException("Access denied, don't have role");
                    }
                }
                ProxyHandlerUtil.handleProxy(proxyUrl, exchange);
            } catch (TokenValidationException e) {
                throw new AccessDeniedException(e.getMessage());
            }
        }catch (AccessDeniedException e){
            exchange.sendResponseHeaders(403, e.getMessage().getBytes().length);
            exchange.getResponseBody().write(e.getMessage().getBytes());
        }
    }

    private boolean Contains(List<String> rolesToAccess, List<String> userRoles){
        Set<String> rolesSet = new HashSet<>(rolesToAccess);
        for(String role: userRoles){
            if(rolesSet.contains(role)){
                return true;
            }
        }
        return false;
    }
}
