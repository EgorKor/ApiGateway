package proxyHandling;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.net.URL;
import java.util.Map;

@AllArgsConstructor
public class HttpProxyHandler implements HttpHandler {
    private Map<String, String> proxyRoutes;

    @SneakyThrows
    @Override
    public void handle(HttpExchange exchange){
        String proxyRoute = proxyRoutes.get(String.valueOf(exchange.getRequestURI()));
        URL proxyUrl = new URL(proxyRoute);
        ProxyHandlerUtil.handleProxy(proxyUrl, exchange);
    }
}
