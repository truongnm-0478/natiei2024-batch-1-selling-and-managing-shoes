package group1.intern.service;

import group1.intern.bean.Credential;
import group1.intern.bean.LoginRequest;

public interface AuthService {
    Credential login(LoginRequest loginRequest);
}
