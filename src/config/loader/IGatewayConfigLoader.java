package config.loader;

import config.model.ApiGatewayConfig;

@FunctionalInterface
public interface IGatewayConfigLoader {
    ApiGatewayConfig loadConfig();
}
