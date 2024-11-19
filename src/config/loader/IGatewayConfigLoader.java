package config.loader;

import config.model.RoutingConfig;

@FunctionalInterface
public interface IGatewayConfigLoader {
    RoutingConfig loadConfig();
}
