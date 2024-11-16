package config.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerConfig {
    private int port;
    private String host;
    private int backlog;
}
