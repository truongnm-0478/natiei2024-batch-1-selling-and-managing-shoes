package group1.intern.util.aspect;

import group1.intern.model.Account;
import group1.intern.model.Enum.AccountRole;
import group1.intern.util.exception.UnauthorizedException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AdminAuthorizationAspect {
    @Before("execution(* group1.intern.controller.admin.AccountsController.*(..)) && args(account,..)")
    public void checkAdminAuthorization(Account account) {
        AccountRole role = account.getRole();
        if (role != AccountRole.ADMIN) {
            throw new UnauthorizedException("You are not authorized to access this page");
        }
    }
}
