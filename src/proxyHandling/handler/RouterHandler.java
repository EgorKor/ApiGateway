package proxyHandling.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import config.model.RoutingConfig;
import config.model.pair.ProxyResponseThisServerExchangePair;
import config.model.pair.ProxyRouteDetails;
import logger.Logger;
import lombok.AllArgsConstructor;
import proxyHandling.util.ProxyHandlerUtil;
import util.Pair;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

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
                exchange.sendResponseHeaders(404, message.getBytes().length);
                exchange.getResponseBody().write(message.getBytes());
                exchange.close();
            }
        }catch (Exception e){
            Logger.error(e.getMessage());
            exchange.sendResponseHeaders(500,e.getMessage().getBytes().length);
            exchange.getResponseBody().write(e.getMessage().getBytes());
            exchange.close();
        }
    }

    private void logException(String message, HttpExchange exchange){
        Logger.warn("Exception occured while handling request %s. Message : %s".formatted(exchange.getRequestURI().toString(),message));
    }

    private Pair<ProxyRouteDetails, String> findProxyRoute(String url) throws IllegalArgumentException{
        for(ProxyRouteDetails details : routingConfig.getTemplateRoutes()){
            if(routeMatches(details.getTemplateRoutePair().getTemplate(), url)){
                if(details.isProxyRequestURL()){
                    return Pair.of(details,details.getTemplateRoutePair().getTemplate() + url);
                }
                return Pair.of(details,details.getTemplateRoutePair().getProxyURL());
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
