package apiGatewayPostActions;

import apiGateway.config.model.pair.ProxyResponseThisServerExchangePair;
import apiGateway.logger.Logger;
import apiGateway.proxyHandling.util.ProxyHandlerUtil;
import com.sun.net.httpserver.HttpExchange;
import lombok.AllArgsConstructor;

import javax.swing.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@AllArgsConstructor
public class AuthenticatePostAction implements Consumer<ProxyResponseThisServerExchangePair> {
    String authURL;


    @Override
    public void accept(ProxyResponseThisServerExchangePair proxyResponseThisServerExchangePair) {
        try {
            HttpURLConnection proxyResponse = proxyResponseThisServerExchangePair.getProxyResponse();
            String responseBody = new String(proxyResponse.getInputStream().readAllBytes());
            Logger.info("Readed %s in postAction".formatted(responseBody));
            String contentType = proxyResponse.getContentType();
            proxyResponse.disconnect();

            HttpURLConnection authConnection = (HttpURLConnection) new URL(authURL).openConnection();
            authConnection.setRequestMethod("POST");
            authConnection.setRequestProperty("content-type", contentType);
            authConnection.setDoOutput(true);
            authConnection.getOutputStream().write(responseBody.getBytes());
            authConnection.getOutputStream().flush();
            int authResponseCode = authConnection.getResponseCode();
            String authResponseBody = new String(authConnection.getInputStream().readAllBytes());
            authConnection.disconnect();

            HttpExchange thisServerExchange = proxyResponseThisServerExchangePair.getThisServerExchange();
            String cookieHeader = "Set-Cookie";
            List<String> cookieValues = authConnection.getHeaderFields().get(cookieHeader);
            thisServerExchange.getResponseHeaders().put(cookieHeader,cookieValues);
            thisServerExchange.getResponseHeaders().put("content-type",List.of(contentType));

            thisServerExchange.sendResponseHeaders(authResponseCode, authResponseBody.length());
            thisServerExchange.getResponseBody().write(authResponseBody.getBytes());

            thisServerExchange.close();
        } catch (Exception e) {
            e.printStackTrace();
            proxyResponseThisServerExchangePair.getThisServerExchange().close();
        }
    }
}
