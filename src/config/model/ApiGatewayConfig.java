package config.model;

import lombok.Getter;
import lombok.Setter;
import util.Pair;

import java.util.*;

@Getter
@Setter
public class ApiGatewayConfig {

    private Map<String, String> proxyRoutes;
    private Map<String, Pair<String, List<String>>> securedProxyRoutes;
    public ApiGatewayConfig(){
        proxyRoutes = new HashMap<>();
        securedProxyRoutes = new HashMap<>();
    }

    public ApiGatewayConfig addProxyRoute(String route, String proxy){
        proxyRoutes.put(route, proxy);
        return this;
    }

    public ApiGatewayConfig addSecuredProxyRoute(String route, String proxy, String ... roles){
        securedProxyRoutes.put(route, Pair.of(proxy, new ArrayList<>(Arrays.stream(roles).toList())));
        return this;
    }

}
