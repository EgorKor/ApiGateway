package auth;

import java.util.List;

public interface UserDetails {
    String getLogin();
    String getPassword();
    List<String> getRoles();
    boolean isBlocked();
}
