package group1.intern.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import group1.intern.annotation.PreAuthorizeAdmin;

@Controller("adminAccountsController")
@PreAuthorizeAdmin
@RequestMapping("/admin/accounts")
public class AccountsController {
    
    @GetMapping("/admin/account-management")
    public String getAccountManagement(Model model) {
        model.addAttribute("currentPage", "account-management"); // account-management, product-management,
                                                                 // bill-management, statistic-management
        return "/screens/admin/accounts/index";
    }
}
