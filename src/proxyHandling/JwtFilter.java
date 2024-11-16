package proxyHandling;

import com.sun.net.httpserver.HttpExchange;

public class JwtFilter {

    public void validateToken(HttpExchange exchange) throws TokenValidationException{
        throw new TokenValidationException();
    }

}
