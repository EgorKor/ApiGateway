package kernel;

import com.sun.net.httpserver.HttpServer;
import config.loader.IConfigLoader;
import config.loader.IGatewayConfigLoader;
import config.model.ApiGatewayConfig;
import config.model.ServerConfig;
import lombok.SneakyThrows;

import java.net.InetSocketAddress;

public class ApiGatewayServer {
    private final HttpServer kernelServer;
    private final ServerConfig serverConfig;
    private final ApiGatewayConfig apiGatewayConfig;

    public ApiGatewayServer(){
        this(() -> ServerConfig
                .builder()
                .backlog(0)
                .host("localhost")
                .port(8080)
                .build(),
                ApiGatewayConfig::new);
    }

    @SneakyThrows
    public ApiGatewayServer(IConfigLoader configLoader, IGatewayConfigLoader gatewayConfigLoader)  {
        this.apiGatewayConfig = gatewayConfigLoader.loadConfig();
        this.serverConfig = configLoader.loadConfig();
        this.kernelServer = HttpServer.create(new InetSocketAddress(serverConfig.getHost(), serverConfig.getPort()),serverConfig.getBacklog());
    }

    public void start(){
        kernelServer.start();
    }

    public ApiGatewayConfig configureGateway(){
        return apiGatewayConfig;
    }

}
