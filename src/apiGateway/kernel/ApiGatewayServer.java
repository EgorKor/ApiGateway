package apiGateway.kernel;

import com.sun.net.httpserver.HttpServer;
import apiGateway.config.loader.IConfigLoader;
import apiGateway.config.loader.IGatewayConfigLoader;
import apiGateway.config.model.RoutingConfig;
import apiGateway.config.model.ServerConfig;
import apiGateway.config.model.pair.ProxyRouteDetails;
import apiGateway.logger.Logger;
import lombok.SneakyThrows;
import apiGateway.proxyHandling.handler.RouterHandler;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiGatewayServer {
    private final HttpServer kernelServer;


    @SneakyThrows
    public ApiGatewayServer(IConfigLoader configLoader, IGatewayConfigLoader gatewayConfigLoader) {
        Logger.startTimer();
        RoutingConfig routingConfig = gatewayConfigLoader.loadConfig();
        ServerConfig serverConfig = configLoader.loadConfig();
        this.kernelServer = HttpServer.create(new InetSocketAddress(serverConfig.getHost(), serverConfig.getPort()), serverConfig.getBacklog());
        ExecutorService executorService = Executors.newFixedThreadPool(serverConfig.getThreadsCount());
        this.kernelServer.setExecutor(executorService);
        this.kernelServer.createContext("/",new RouterHandler(routingConfig));
        logConfig(routingConfig,serverConfig);
    }

    public void start() {
        kernelServer.start();
        Logger.stopTimer();
        Logger.info("Server starts in %s seconds".formatted(Logger.getTimerValue()));
    }


    private void logConfig(RoutingConfig routingConfig, ServerConfig serverConfig){
        Logger.info("Server url : http://%s:%d".formatted(serverConfig.getHost(), serverConfig.getPort()));
        Logger.info("Server backlog : %d".formatted(serverConfig.getBacklog()));
        Logger.info("Server threads : %d".formatted(serverConfig.getThreadsCount()));
        List<ProxyRouteDetails> proxyRouteDetailsList = routingConfig.getTemplateRoutes();
        for (ProxyRouteDetails details: proxyRouteDetailsList) {
            Logger.info(logRoute(details));
        }
    }

    private String logRoute(ProxyRouteDetails details){
        return "baseUrlTemplate: %s , proxyRoute: %s\nproxyRequestURL: %b, postAction = %b".formatted(details.getTemplateRoutePair().getTemplate(),
                details.getTemplateRoutePair().getProxyURL(),details.isProxyRequestURL(), details.getProxyPostAction() != null);
    }
}
