package group1.intern.service;

import group1.intern.bean.Credential;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    UserDetails getAccountFromToken(String token);

    Credential generateToken(int accountId);

    Credential refreshToken(String refreshToken);
}
