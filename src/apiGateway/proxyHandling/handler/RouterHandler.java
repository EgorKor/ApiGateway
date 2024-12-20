package apiGateway.proxyHandling.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import apiGateway.config.model.RoutingConfig;
import apiGateway.config.model.pair.ProxyResponseThisServerExchangePair;
import apiGateway.config.model.pair.ProxyRouteDetails;
import apiGateway.logger.Logger;
import lombok.AllArgsConstructor;
import apiGateway.proxyHandling.util.ProxyHandlerUtil;
import apiGateway.util.Pair;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Класс реализующий обработку всех запросов и роутинг этих запросов
 * для дальнейшего проксировния
 * */
@AllArgsConstructor
public class RouterHandler implements HttpHandler {
    private RoutingConfig routingConfig;

    /**
     * Метод обработки проксируемого запроса. Сначала берётся URL и по нему ищут прокси
     * URL на который нужно отправить этот запрос. Если не найден роут, то возврщается код 404
     * с сообщением об ошибке. Если роут найден и в нём нет postAction то, запрос проксируется
     * без дополнительной обработки, а если есть, то после возврата ответа с проксируемого
     * ресурса вызывается postAction который завершает обработку проксирования*/
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            exchange.getResponseHeaders().put("Access-Control-Allow-Origin", List.of("*"));
            exchange.getResponseHeaders().put("Access-Control-Allow-Methods",List.of("GET, POST, PUT, DELETE"));
            exchange.getResponseHeaders().put("Access-Control-Allow-Headers",List.of("Content-Type, Authorization"));
            exchange.getResponseHeaders().put("Access-Control-Allow-Credentials",List.of("true"));
            exchange.getResponseHeaders().put("Access-Control-Expose-Headers",List.of("Content-Length, Content-Type"));
            exchange.getResponseHeaders().add("Access-Control-Max-Age", "3600");
            if(exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")){
                sendCORSResponse(exchange);
                return;
            }
            String url = String.valueOf(exchange.getRequestURI());
            try {
                Pair<ProxyRouteDetails, String> details = findProxyRoute(exchange.getRequestURI().toString());
                if (!details.getFirst().isProxyRequestURL()) {
                    HttpURLConnection response = ProxyHandlerUtil.sendProxyRequest(new URL(details.getSecond()), exchange);
                    details.getFirst().getProxyPostAction().accept(ProxyResponseThisServerExchangePair.of(response, exchange));
                } else {
                    ProxyHandlerUtil.sendProxyResponse(new URL(details.getSecond()), exchange);
                }
            } catch (IllegalArgumentException e) {
                String message = e.getMessage();
                logException(e.getMessage(), exchange);
                e.printStackTrace();
                exchange.sendResponseHeaders(404, message.getBytes().length);
                exchange.getResponseBody().write(message.getBytes());
                exchange.close();
            }
        }catch (Exception e){
            Logger.error(e.getMessage());
            e.printStackTrace();
            exchange.sendResponseHeaders(500,e.getMessage().getBytes().length);
            exchange.getResponseBody().write(e.getMessage().getBytes());
            exchange.close();
        }
    }

    private void sendCORSResponse(HttpExchange exchange) throws IOException {

        exchange.sendResponseHeaders(204,-1);
        exchange.close();
    }

    private void logException(String message, HttpExchange exchange){
        Logger.warn("Exception occured while handling request %s. Message : %s".formatted(exchange.getRequestURI().toString(),message));
    }

    private Pair<ProxyRouteDetails, String> findProxyRoute(String url) throws IllegalArgumentException{
        for(ProxyRouteDetails details : routingConfig.getTemplateRoutes()){
            if(routeMatches(details.getTemplateRoutePair().getTemplate(), url)){
                if(!details.isProxyRequestURL()){
                    return Pair.of(details,details.getTemplateRoutePair().getProxyURL());
                }
                return Pair.of(details,details.getTemplateRoutePair().getProxyURL() + url);
            }
        }
        throw new IllegalArgumentException("no proxy for this route");
    }

    private boolean routeMatches(String template, String url){
        if(template.contains("*")){
            return url.contains(template.replaceAll("/\\*+",""));
        }else{
            return template.equals(url);
        }
    }

}
