package proxyHandling;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

@AllArgsConstructor
public class HttpSecuredProxyHandler implements HttpHandler {
    private Map<String, String> securedRoutes;
    private JwtFilter jwtFilter;

    @SneakyThrows
    @Override
    public void handle(HttpExchange exchange) {
        String proxyRoute = securedRoutes.get(String.valueOf(exchange.getRequestURI()));
        URL proxyUrl = new URL(proxyRoute);
        try{
            jwtFilter.validateToken(exchange);
            ProxyHandlerUtil.handleProxy(proxyUrl, exchange);
        }catch (TokenValidationException e){
            //TODO обработать ошибку аутентификации - по сути вернуть 403 код
        }
    }
}
