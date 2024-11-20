package apiGateway.config.loader;

import apiGateway.config.model.ServerConfig;

@FunctionalInterface
public interface IConfigLoader {
    ServerConfig loadConfig();
}
