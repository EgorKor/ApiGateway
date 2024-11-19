import config.loader.IConfigLoader;
import config.loader.IGatewayConfigLoader;
import config.model.RoutingConfig;
import config.model.ServerConfig;
import kernel.ApiGatewayServer;
import proxyHandling.util.ProxyHandlerUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {
    public static void main(String[] args) {
        IConfigLoader iConfigLoader = () -> ServerConfig.builder()
                .threadsCount(50)
                .backlog(500)
                .host("localhost")
                .port(8080)
                .build();
        IGatewayConfigLoader iGatewayConfigLoader = () ->
                RoutingConfig.builder().addRoute("/api/v1/calcService/getAccess", "http://localhost:8082/api/v1/auth/echo", (o) -> {
                    try {
                        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8081/api/v1/calcService/getAccess").openConnection();
                        connection.setRequestMethod("GET");
                        connection.connect();
                        connection.getResponseCode();
                        ProxyHandlerUtil.copyResponseHeaders(connection, o.getThisServerExchange());
                        o.getThisServerExchange().sendResponseHeaders(connection.getResponseCode(), connection.getContentLengthLong());
                        ProxyHandlerUtil.copyResponseBody(connection, o.getThisServerExchange());
                        o.getThisServerExchange().close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },false);
        ApiGatewayServer apiGatewayServer = new ApiGatewayServer(iConfigLoader, iGatewayConfigLoader);
        apiGatewayServer.start();
    }
}
