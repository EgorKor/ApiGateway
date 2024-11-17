package auth;

public interface UserDetailsService {
    UserDetails loadUser(String username);
}
