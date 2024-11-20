import apiGateway.config.loader.IConfigLoader;
import apiGateway.config.loader.IGatewayConfigLoader;
import apiGateway.config.model.RoutingConfig;
import apiGateway.config.model.ServerConfig;
import apiGateway.kernel.ApiGatewayServer;
import apiGateway.proxyHandling.util.ProxyHandlerUtil;
import apiGatewayPostActions.AuthenticatePostAction;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        IConfigLoader iConfigLoader = () -> ServerConfig.builder()
                .threadsCount(50)
                .backlog(500)
                .host("localhost")
                .port(8080)
                .build();
        IGatewayConfigLoader iGatewayConfigLoader = () ->
                RoutingConfig.builder().addRoute("/api/v1/calcService/auth/getAccess", "http://localhost:8082/api/v1/auth",
                        new AuthenticatePostAction("http://localhost:8081/api/v1/calcService/auth/getAccess"),false)
                        .addRoute("/api/v1/calcService/*","http://localhost:8081",true);
        ApiGatewayServer apiGatewayServer = new ApiGatewayServer(iConfigLoader, iGatewayConfigLoader);
        apiGatewayServer.start();
    }
}
