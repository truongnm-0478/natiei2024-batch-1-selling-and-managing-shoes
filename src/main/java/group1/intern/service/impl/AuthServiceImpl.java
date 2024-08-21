package group1.intern.service.impl;

import group1.intern.bean.AccountRegistration;
import group1.intern.bean.Credential;
import group1.intern.bean.LoginRequest;
import group1.intern.model.Account;
import group1.intern.model.Enum.AccountRole;
import group1.intern.model.RefreshToken;
import group1.intern.repository.AccountRepository;
import group1.intern.repository.RefreshTokenRepository;
import group1.intern.service.AuthService;
import group1.intern.service.JwtService;
import group1.intern.util.exception.BadRequestException;
import group1.intern.util.exception.DuplicateEmailException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Credential login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Account account = (Account) authentication.getPrincipal();
        Credential response = jwtService.generateToken(account.getId());
        // Save the refresh token
        refreshTokenRepository.save(RefreshToken.builder().account(account).token(response.getRefreshToken()).build());
        return response;
    }

    @Override
    public Account register(AccountRegistration accountRegistration) {
        if (accountRepository.findByEmail(accountRegistration.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email đã tồn tại: " + accountRegistration.getEmail());
        }

        String encodedPassword = passwordEncoder.encode(accountRegistration.getPassword());

        Account account = Account.builder()
            .email(accountRegistration.getEmail())
            .fullName(accountRegistration.getFullName())
            .password(encodedPassword)
            .role(AccountRole.CUSTOMER)
            .address(accountRegistration.getAddress())
            .phoneNumber(accountRegistration.getPhoneNumber())
            .gender(true)
            .isActivated(true)
            .build();

        try {
            return accountRepository.save(account);
        } catch (Exception e) {
            throw new BadRequestException("Lưu tài khoản không thành công");
        }
    }
}
