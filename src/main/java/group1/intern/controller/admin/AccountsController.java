package group1.intern.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import group1.intern.annotation.PreAuthorizeAdmin;

@Controller
public class AccountsController {
    @PreAuthorizeAdmin
    @GetMapping("/admin/account-management")
    public String getAccountManagement(Model model) {
        model.addAttribute("currentPage", "account-management"); // account-management, product-management,
                                                                 // bill-management, statistic-management
        return "/screens/admin/accounts/index";
    }
}
