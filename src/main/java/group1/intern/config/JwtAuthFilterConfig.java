package group1.intern.config;

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
import org.springframework.security.core.userdetails.UserDetails;
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

        Cookie accessToken = WebUtils.getCookie(req, CommonConstant.ACCESS_TOKEN);
        Cookie refreshToken = WebUtils.getCookie(req, CommonConstant.REFRESH_TOKEN);
        try {
            // Check access token in the cookie
            if (accessToken == null || CommonUtils.isEmptyOrNullString(accessToken.getValue())) {
                filterChain.doFilter(req, resp);
                return;
            }

            // Set new authentication object to the SecurityContextHolder
            UserDetails userDetails = jwtService.getAccountFromToken(accessToken.getValue());
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, accessToken, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(req, resp);
        } catch (UnauthorizedException e) {
            if (e.getMessage().equalsIgnoreCase(ErrorMessageConstant.EXPIRED_TOKEN) && refreshToken != null && CommonUtils.isNotEmptyOrNullString(refreshToken.getValue())) {
                var credential = jwtService.refreshToken(refreshToken.getValue());
                WebUtils.setCookie(resp, CommonConstant.ACCESS_TOKEN, credential.getAccessToken());
                WebUtils.setCookie(resp, CommonConstant.REFRESH_TOKEN, credential.getRefreshToken());
                resp.sendRedirect(req.getRequestURI());
            } else {
                WebUtils.removeCookie(resp, CommonConstant.ACCESS_TOKEN);
                WebUtils.removeCookie(resp, CommonConstant.REFRESH_TOKEN);
                throw e;
            }
        }
    }
}
