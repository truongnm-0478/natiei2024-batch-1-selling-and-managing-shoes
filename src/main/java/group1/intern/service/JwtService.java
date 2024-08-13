package group1.intern.service;

import group1.intern.bean.Credential;
import group1.intern.model.Account;

public interface JwtService {
    Account getAccountFromToken(String token);

    Credential generateToken(int accountId);

    Credential refreshToken(String refreshToken);
}
