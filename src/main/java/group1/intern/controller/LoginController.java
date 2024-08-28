package group1.intern.controller;

import group1.intern.bean.AccountInfo;
import group1.intern.bean.LoginRequest;
import group1.intern.bean.ToastMessage;
import group1.intern.model.Account;
import group1.intern.model.Enum.AccountRole;
import group1.intern.service.AuthService;
import group1.intern.util.constant.CommonConstant;
import group1.intern.util.util.CommonUtils;
import group1.intern.util.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final AuthService authService;

    @GetMapping("/login")
    public String view(Model model) {
        var currentAccount = WebUtils.Sessions.getAttribute(CommonConstant.CURRENT_USER, AccountInfo.class);
        if (currentAccount != null) {
            // Redirect to previous URL if current user role is customer
            return currentAccount.getRole() == 1 ?
                redirectPreviousUrl() :
                "redirect:/admin/statistic";
        }
        model.addAttribute("loginRequest", new LoginRequest());
        return "screens/auth/login";
    }

    @PostMapping("/login")
    public String login(
        @ModelAttribute("loginRequest") LoginRequest loginRequest,
        Model model
    ) {
        try {
            var credential = authService.login(loginRequest);
            // Save token to cookie
            WebUtils.Cookies.setCookie(CommonConstant.ACCESS_TOKEN, credential.getAccessToken());
            WebUtils.Cookies.setCookie(CommonConstant.REFRESH_TOKEN, credential.getRefreshToken());
            // Save account to session
            Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            var accountInfo = AccountInfo.fromAccount(account);
            WebUtils.Sessions.setAttribute(CommonConstant.CURRENT_USER, accountInfo);

            // Redirect to previous URL if current user role is customer
            return account.getRole() == AccountRole.CUSTOMER ?
                redirectPreviousUrl() :
                "redirect:/admin/statistic";
        } catch (BadCredentialsException ex) {
            model.addAttribute("toastMessages", new ToastMessage("error", "Địa chỉ email hoặc mật khẩu không chính xác!"));
            return "screens/auth/login";
        } catch (InternalAuthenticationServiceException ex) {
            model.addAttribute("toastMessages", new ToastMessage("error", "Tài khoản không tồn tại!"));
            return "screens/auth/login";
        } catch (DisabledException ex) {
            model.addAttribute("toastMessages", new ToastMessage("error", "Tài khoản đã bị khóa!"));
            return "screens/auth/login";
        }
    }

    @GetMapping("/log-out")
    public String logout() {
        WebUtils.Cookies.removeCookie(CommonConstant.ACCESS_TOKEN);
        WebUtils.Cookies.removeCookie(CommonConstant.REFRESH_TOKEN);
        WebUtils.Sessions.removeAttribute(CommonConstant.CURRENT_USER);
        return "redirect:/";
    }

    private String redirectPreviousUrl() {
        var previousUrl = WebUtils.Sessions.getAttribute(CommonConstant.PREVIOUS_GET_URL, String.class);
        if (CommonUtils.isNotEmptyOrNullString(previousUrl) && !previousUrl.contains("/login") && !previousUrl.contains("/register")) {
            return String.format("redirect:%s", previousUrl);
        }
        return "redirect:/";
    }
}
