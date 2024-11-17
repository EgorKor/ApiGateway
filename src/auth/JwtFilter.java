package auth;

import auth.AuthAdapter;
import auth.UserDetailsService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.sun.net.httpserver.HttpExchange;
import config.model.AuthConfig;
import lombok.AllArgsConstructor;
import proxyHandling.TokenValidationException;

@AllArgsConstructor
public class JwtFilter {
    private AuthConfig authConfig;
    private UserDetailsService userDetailsService;
    public UserDetails validateTokenAndExtractUserInfo(HttpExchange exchange) throws TokenValidationException {
        String token = exchange.getRequestHeaders().getFirst("authorization");
        if(token == null){
            throw new TokenValidationException("Missing authorization header");
        }
        if(!token.contains("bearer ")){
            throw new TokenValidationException("Authorization Header should starts with 'bearer'");
        }
        token = token.substring(7);
        if(token.isBlank()){
            throw new TokenValidationException("Token should not be empty or blank");
        }
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(authConfig.getSecretKey())).build();
            jwtVerifier.verify(token);
            return userDetailsService.loadUser(JWT.decode(token).getClaim("username").asString());
        } catch (JWTVerificationException e){
            throw new TokenValidationException(e.getMessage());
        }
    }

}
