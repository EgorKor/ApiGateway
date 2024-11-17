package proxyHandling;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenValidationException extends Exception{
    private String message;
}
