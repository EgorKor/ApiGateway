package config.model;

import lombok.Builder;
import lombok.Data;

/**
 * Класс инкапсулирующий сетевую конфигурацию
 * сервера
 * */
@Data
@Builder
public class ServerConfig {
    private int port;
    private String host;
    private int backlog;
    private int threadsCount;
}
