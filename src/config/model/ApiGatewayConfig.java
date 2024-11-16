package config.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ApiGatewayConfig {

    private Map<String, String> proxyRoutes;
    private Map<String, String> securedProxyRoutes;
    public ApiGatewayConfig(){
        proxyRoutes = new HashMap<>();
        securedProxyRoutes = new HashMap<>();
    }

    public ApiGatewayConfig addProxyRoute(String route, String proxy){
        proxyRoutes.put(route, proxy);
        return this;
    }

    public ApiGatewayConfig addSecuredProxyRoute(String route, String proxy){
        securedProxyRoutes.put(route, proxy);
        return this;
    }

}
