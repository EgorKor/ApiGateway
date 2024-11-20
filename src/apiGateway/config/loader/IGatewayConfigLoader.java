package apiGateway.config.loader;

import apiGateway.config.model.RoutingConfig;

@FunctionalInterface
public interface IGatewayConfigLoader {
    RoutingConfig loadConfig();
}
