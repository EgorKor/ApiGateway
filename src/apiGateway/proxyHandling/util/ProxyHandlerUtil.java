package apiGateway.proxyHandling.util;

import com.sun.net.httpserver.HttpExchange;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Класс предоставляющий утилитные методы для работы с проксируемыми запросами
 */
public class ProxyHandlerUtil {
    private ProxyHandlerUtil() {
    }

    /**
     * Метод для проксирования запроса на адрес proxyUrl, данные для проксирования берутся из
     * HttpExchange, копируются заголовки и тело запроса. Результат работы метода - объект из которого
     * можно вытащить Response
     */
    @SneakyThrows
    public static HttpURLConnection sendProxyRequest(URL proxyUrl, HttpExchange exchange) {
        HttpURLConnection connection = (HttpURLConnection) proxyUrl.openConnection();
        //копируем заголовки
        copyRequestHeaders(connection, exchange);
        connection.setRequestMethod(exchange.getRequestMethod());
        //копируем тело
        if (exchange.getRequestMethod().equalsIgnoreCase("POST") ||
                exchange.getRequestMethod().equalsIgnoreCase("PUT") ||
                exchange.getRequestMethod().equalsIgnoreCase("PATCH") ||
                exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            copyRequestBody(connection, exchange);
        }
        connection.connect();
        connection.getResponseCode();
        return connection;
    }

    /**
     * Метод для проксирования запроса на адрес proxyUrl м возврата ответа изначальному
     * отправителю
     */
    @SneakyThrows
    public static void sendProxyResponse(URL proxyUrl, HttpExchange exchange) {
        HttpURLConnection connection = sendProxyRequest(proxyUrl, exchange);
        String responseBody = new String(connection.getInputStream().readAllBytes());
        int responseCode = connection.getResponseCode();
        connection.disconnect();
        //копируем заголовки ответа
        exchange.sendResponseHeaders(responseCode, responseBody.getBytes().length);
        copyResponseHeaders(connection, exchange);
        //копируем
        exchange.getResponseHeaders().put("content-type", List.of("application/json"));
        exchange.getResponseBody().write(responseBody.getBytes());
        exchange.close();
    }

    @SneakyThrows
    public static void copyRequestHeaders(HttpURLConnection connection, HttpExchange exchange) {
        connection.setRequestMethod(exchange.getRequestMethod());
        connection.setDoOutput(true);
        exchange.getRequestHeaders().forEach((key, values) -> {
            for (String value : values) {
                connection.addRequestProperty(key, value);
            }
        });
    }

    @SneakyThrows
    public static void copyRequestBody(HttpURLConnection connection, HttpExchange exchange) {
        try (InputStream is = exchange.getRequestBody();
             OutputStream os = connection.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    @SneakyThrows
    public static void copyResponseBody(HttpURLConnection connection, HttpExchange exchange) {
        try (InputStream is = connection.getInputStream();
             OutputStream os = exchange.getResponseBody()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    @SneakyThrows
    public static void copyResponseHeaders(HttpURLConnection connection, HttpExchange exchange) {
        connection.getHeaderFields().forEach((key, values) -> {
            if (key != null) {
                for (String value : values) {
                    exchange.getResponseHeaders().add(key, value);
                }
            }
        });
    }



}
