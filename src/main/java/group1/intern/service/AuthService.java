package group1.intern.service;

import group1.intern.bean.*;
import group1.intern.model.Account;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {
    Credential login(LoginRequest loginRequest);

    Account register(AccountRegistration accountRegistration);

    Account updateProfile(ProfileInfo profileInfo);

    Account updatePassword(PasswordInfo passwordInfo);

    Account updateAvatar(MultipartFile image, Account account);
}
