package group1.intern.config;

import group1.intern.bean.AccountInfo;
import group1.intern.model.Account;
import group1.intern.service.JwtService;
import group1.intern.util.constant.CommonConstant;
import group1.intern.util.constant.ErrorMessageConstant;
import group1.intern.util.exception.UnauthorizedException;
import group1.intern.util.util.CommonUtils;
import group1.intern.util.util.WebUtils;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilterConfig extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
        @Nonnull HttpServletRequest req,
        @Nonnull HttpServletResponse resp,
        @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {
        if (pathMatcher.match("/assets/**", req.getRequestURI())) {
            filterChain.doFilter(req, resp);
            return;
        }

        Cookie accessToken = WebUtils.Cookies.getCookie(CommonConstant.ACCESS_TOKEN);
        Cookie refreshToken = WebUtils.Cookies.getCookie(CommonConstant.REFRESH_TOKEN);
        try {
            // Check access token in the cookie
            if (accessToken == null || CommonUtils.isEmptyOrNullString(accessToken.getValue())) {
                filterChain.doFilter(req, resp);
                return;
            }

            // Set new authentication object to the SecurityContextHolder
            setAuthentication(accessToken.getValue(), req);

            filterChain.doFilter(req, resp);
        } catch (UnauthorizedException e) {
            try {
                if (e.getMessage().equalsIgnoreCase(ErrorMessageConstant.EXPIRED_TOKEN) &&
                    refreshToken != null &&
                    CommonUtils.isNotEmptyOrNullString(refreshToken.getValue())
                ) {
                    var credential = jwtService.refreshToken(refreshToken.getValue());
                    WebUtils.Cookies.setCookie(CommonConstant.ACCESS_TOKEN, credential.getAccessToken());
                    WebUtils.Cookies.setCookie(CommonConstant.REFRESH_TOKEN, credential.getRefreshToken());
                    setAuthentication(credential.getAccessToken(), req);
                    filterChain.doFilter(req, resp);
                } else {
                    clearDataInWeb();
                    filterChain.doFilter(req, resp);
                }
            } catch (Exception ex) {
                clearDataInWeb();
                filterChain.doFilter(req, resp);
            }
        } catch (Exception e) {
            clearDataInWeb();
            filterChain.doFilter(req, resp);
        }
    }

    private void setAuthentication(String accessToken, HttpServletRequest req) {
        Account userDetails = jwtService.getAccountFromToken(accessToken);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, accessToken, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // Set account info to session
        var accountInfo = AccountInfo.fromAccount(userDetails);
        WebUtils.Sessions.setAttribute(CommonConstant.CURRENT_USER, accountInfo);
    }

    private void clearDataInWeb() {
        WebUtils.Sessions.removeAllData();
        WebUtils.Cookies.removeCookie(CommonConstant.ACCESS_TOKEN);
        WebUtils.Cookies.removeCookie(CommonConstant.REFRESH_TOKEN);
    }
}
