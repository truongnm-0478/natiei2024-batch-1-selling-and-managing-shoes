package group1.intern.service;

import group1.intern.bean.AccountRegistration;
import group1.intern.bean.Credential;
import group1.intern.bean.LoginRequest;
import group1.intern.model.Account;

public interface AuthService {
    Credential login(LoginRequest loginRequest);
    Account register(AccountRegistration accountRegistration);

}
