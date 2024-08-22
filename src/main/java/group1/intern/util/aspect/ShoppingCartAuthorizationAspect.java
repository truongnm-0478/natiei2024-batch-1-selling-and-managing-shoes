package group1.intern.util.aspect;

import group1.intern.annotation.CurrentAccount;
import group1.intern.model.Account;
import group1.intern.util.exception.UnauthorizedException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ShoppingCartAuthorizationAspect {

    @Before("execution(* group1.intern.controller.customer.ShoppingCartsController.*(..)) && args(account,..)")
    public void checkAuthorization(Account account) {
        if (account == null) {
            throw new UnauthorizedException("You need to login to perform action in cart !");
        }
    }
}
