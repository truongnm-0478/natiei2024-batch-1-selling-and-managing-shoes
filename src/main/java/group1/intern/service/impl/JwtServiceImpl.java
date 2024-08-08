package group1.intern.service.impl;

import group1.intern.bean.Credential;
import group1.intern.config.JwtApplicationProperty;
import group1.intern.model.Account;
import group1.intern.repository.AccountRepository;
import group1.intern.repository.RefreshTokenRepository;
import group1.intern.service.JwtService;
import group1.intern.util.constant.CommonConstant;
import group1.intern.util.constant.ErrorMessageConstant;
import group1.intern.util.exception.ForbiddenException;
import group1.intern.util.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.sql.Timestamp;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtApplicationProperty jwtAppProperty;
    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public UserDetails getAccountFromToken(String token) {
        Claims jwtClaims = getJwtClaims(token, TokenType.ACCESS_TOKEN);
        int accountId = Integer.parseInt(jwtClaims.getSubject());

        return accountRepository.findById(accountId)
            .orElseThrow(() -> new ForbiddenException(ErrorMessageConstant.FORBIDDEN));
    }

    @Override
    public Credential generateToken(int accountId) {
        String accessToken = generateAccessToken(accountId);
        return Credential.builder()
            .accessToken(accessToken)
            .refreshToken(generateRefreshToken(accountId))
            .expiredAt(new Timestamp(getJwtClaims(accessToken, TokenType.ACCESS_TOKEN).getExpiration().getTime()).toString())
            .type(CommonConstant.JWT_TYPE)
            .build();
    }

    @Override
    public Credential refreshToken(String refreshToken) {
        Claims jwtClaims = getJwtClaims(refreshToken, TokenType.REFRESH_TOKEN);
        int accountId = Integer.parseInt(jwtClaims.getSubject());
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ForbiddenException(ErrorMessageConstant.FORBIDDEN));
        var accountRefreshToken = refreshTokenRepository.findByAccount_Id(accountId).orElse(null);
        if (accountRefreshToken != null && accountRefreshToken.getToken().equals(refreshToken)) {
            String accessToken = generateAccessToken(accountId);
            return Credential.builder()
                .accessToken(accessToken)
                .type(CommonConstant.JWT_TYPE)
                .refreshToken(accountRefreshToken.getToken())
                .expiredAt(
                    new Timestamp(getJwtClaims(accessToken, TokenType.ACCESS_TOKEN).getExpiration().getTime()).toString())
                .build();
        }
        throw new ForbiddenException(ErrorMessageConstant.REFRESH_TOKEN_NOT_FOUND);
    }

    private String generateAccessToken(int accountId) {
        return Jwts.builder()
            .subject(String.valueOf(accountId))
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtAppProperty.getAccessTokenExpirationMs()))
            .signWith(getSignInKey(jwtAppProperty.getAccessTokenSecret()))
            .compact();
    }

    private String generateRefreshToken(int accountId) {
        return Jwts.builder()
            .subject(String.valueOf(accountId))
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtAppProperty.getRefreshTokenExpirationMs()))
            .signWith(getSignInKey(jwtAppProperty.getRefreshTokenSecret()))
            .compact();
    }

    private Claims getJwtClaims(String token, TokenType tokenType) {
        switch (tokenType) {
            case ACCESS_TOKEN:
                try {
                    return Jwts.parser()
                        .verifyWith(getSignInKey(jwtAppProperty.getAccessTokenSecret()))
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
                } catch (ExpiredJwtException ex) {
                    throw new UnauthorizedException(ErrorMessageConstant.EXPIRED_TOKEN);
                } catch (Exception ex) {
                    throw new UnauthorizedException(ErrorMessageConstant.INVALID_TOKEN);
                }
            case REFRESH_TOKEN:
                try {
                    return Jwts.parser()
                        .verifyWith(getSignInKey(jwtAppProperty.getRefreshTokenSecret()))
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
                } catch (ExpiredJwtException ex) {
                    throw new UnauthorizedException(ErrorMessageConstant.EXPIRED_REFRESH_TOKEN);
                } catch (Exception ex) {
                    throw new UnauthorizedException(ErrorMessageConstant.INVALID_REFRESH_TOKEN);
                }
            default:
                throw new UnauthorizedException(ErrorMessageConstant.INVALID_TOKEN);
        }
    }

    private SecretKey getSignInKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    enum TokenType {
        ACCESS_TOKEN, REFRESH_TOKEN
    }
}
