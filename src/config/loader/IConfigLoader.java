package config.loader;

import config.model.ServerConfig;

@FunctionalInterface
public interface IConfigLoader {
    ServerConfig loadConfig();
}
