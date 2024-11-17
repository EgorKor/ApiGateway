package auth;

public interface AuthAdapter {

    void authenticate(UserDetails userDetails) throws Exception;
}
