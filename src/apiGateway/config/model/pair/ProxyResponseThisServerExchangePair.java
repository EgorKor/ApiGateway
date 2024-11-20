package apiGateway.config.model.pair;

import com.sun.net.httpserver.HttpExchange;
import lombok.Getter;
import lombok.Setter;

import java.net.HttpURLConnection;

/** Класс инкапсулирующий ответ с проксирукмого сервера и объект размена первичного запроса
 * на прокси сервер*/
@Getter
@Setter
public class ProxyResponseThisServerExchangePair {
    private HttpURLConnection proxyResponse;
    private HttpExchange thisServerExchange;

    private ProxyResponseThisServerExchangePair(HttpURLConnection proxyResponse, HttpExchange thisServerExchange) {
        this.proxyResponse = proxyResponse;
        this.thisServerExchange = thisServerExchange;
    }

    public static ProxyResponseThisServerExchangePair of(HttpURLConnection proxyResponse, HttpExchange thisServerExchange){
        return new ProxyResponseThisServerExchangePair(proxyResponse, thisServerExchange);
    }
}
