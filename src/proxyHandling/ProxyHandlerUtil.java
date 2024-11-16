package proxyHandling;

import com.sun.net.httpserver.HttpExchange;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProxyHandlerUtil {
    private ProxyHandlerUtil() {
    }

    @SneakyThrows
    public static void handleProxy(URL proxyUrl, HttpExchange exchange) {
        HttpURLConnection connection = (HttpURLConnection) proxyUrl.openConnection();
        //копируем заголовки
        copyRequestHeaders(connection, exchange);
        //копируем тело
        if (exchange.getRequestMethod().equalsIgnoreCase("POST") ||
                exchange.getRequestMethod().equalsIgnoreCase("PUT") ||
                exchange.getRequestMethod().equalsIgnoreCase("PATCH")) {
            copyRequestBody(connection, exchange);
        }
        int responseCode = connection.getResponseCode();
        //копируем заголовки ответа
        copyResponseHeaders(connection, exchange);
        exchange.sendResponseHeaders(responseCode, connection.getContentLengthLong());
        //копируем
        copyResponseBody(connection, exchange);
        exchange.close();
    }

    @SneakyThrows
    private static void copyRequestHeaders(HttpURLConnection connection, HttpExchange exchange) {
        connection.setRequestMethod(exchange.getRequestMethod());
        connection.setDoOutput(true);
        exchange.getRequestHeaders().forEach((key, values) -> {
            for (String value : values) {
                connection.addRequestProperty(key, value);
            }
        });
    }

    @SneakyThrows
    private static void copyRequestBody(HttpURLConnection connection, HttpExchange exchange) {
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
    private static void copyResponseBody(HttpURLConnection connection, HttpExchange exchange) {
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
    private static void copyResponseHeaders(HttpURLConnection connection, HttpExchange exchange) {
        connection.getHeaderFields().forEach((key, values) -> {
            if (key != null) {
                for (String value : values) {
                    exchange.getResponseHeaders().add(key, value);
                }
            }
        });
    }

}
