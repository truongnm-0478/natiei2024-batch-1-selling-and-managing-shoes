package group1.intern.service.impl;

import group1.intern.bean.*;
import group1.intern.model.Account;
import group1.intern.model.Enum.AccountRole;
import group1.intern.model.RefreshToken;
import group1.intern.repository.AccountRepository;
import group1.intern.repository.RefreshTokenRepository;
import group1.intern.service.AuthService;
import group1.intern.service.CloudinaryService;
import group1.intern.service.JwtService;
import group1.intern.util.exception.BadRequestException;
import group1.intern.util.exception.DuplicateEmailException;
import group1.intern.util.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

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
    public Account register(AccountRegistration accountRegistration, AccountRole role) {
        if (role == AccountRole.ADMIN)
            throw new ForbiddenException("Không thể tạo tài khoản admin");

        if (accountRepository.findByEmail(accountRegistration.getEmail()).isPresent())
            throw new DuplicateEmailException("Email đã tồn tại: " + accountRegistration.getEmail());

        String encodedPassword = passwordEncoder.encode(accountRegistration.getPassword());

        Account account = Account.builder()
            .email(accountRegistration.getEmail())
            .fullName(accountRegistration.getFullName())
            .displayName(accountRegistration.getFullName())
            .password(encodedPassword)
            .role(role)
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

    @Override
    public Account updateProfile(ProfileInfo profileInfo) {
        Account account = accountRepository.findById(profileInfo.getAccountId()).orElse(null);

        if (account == null) {
            return null;
        } else {

            // cap nhap cac thong tin profile ( ngoai tru email)
            account.setFullName(profileInfo.getFullName());
            account.setAddress(profileInfo.getAddress());
            account.setPhoneNumber(profileInfo.getPhoneNumber());
            account.setGender(profileInfo.getGender());
            account.setDisplayName(profileInfo.getDisplayName());
            account.setDateOfBirth(profileInfo.getDateOfBirth());

            try {
                return accountRepository.save(account);
            } catch (Exception e) {
                throw new BadRequestException("Cập nhập tài khoản không thành công");
            }
        }
    }

    @Override
    public Account updatePassword(PasswordInfo passwordInfo) {
        Account account = accountRepository.findById(passwordInfo.getAccountId()).orElse(null);
        String encodedPassword = passwordEncoder.encode(passwordInfo.getPassword());
        if (account == null) {
            return null;
        } else {
            // cap nhap mat khau
            account.setPassword(encodedPassword);
            try {
                return accountRepository.save(account);
            } catch (Exception e) {
                throw new BadRequestException("Cập nhập mật khẩu không thành công");
            }
        }
    }

    @Override
    public Account updateAvatar(MultipartFile image, Account account) {

        Map<String, Object> uploadResult = cloudinaryService.uploadFile(image);
        String imageUrl = (String) uploadResult.get("url");
        String publicId = (String) uploadResult.get("public_id");
        account.setAvatarUrl(imageUrl);

        try {
            return accountRepository.save(account);
        } catch (Exception e) {
            throw new BadRequestException("Cập nhập ảnh đại diện không thành công");
        }
    }
}
