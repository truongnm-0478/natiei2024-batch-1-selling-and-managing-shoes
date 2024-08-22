package group1.intern.controller.admin;

import group1.intern.annotation.CurrentAccount;
import group1.intern.bean.AccountActivateForm;
import group1.intern.bean.AccountFilter;
import group1.intern.model.Account;
import group1.intern.service.AccountsService;
import group1.intern.util.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import group1.intern.annotation.PreAuthorizeAdmin;

@Controller("adminAccountsController")
@PreAuthorizeAdmin
@RequestMapping("/admin/accounts")
public class AccountsController {

    private final AccountsService accountService;

    @Autowired
    public AccountsController(AccountsService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public String index(
        @CurrentAccount Account account,
        @ModelAttribute AccountFilter filter,
        Model model
    ) {
        Page<Account> accounts = accountService.findAccountsByFilter(
            filter.getPage(), 24, filter.getOrder(), filter.getRole(),
            filter.getSortBy(), filter.getQuery()
        );

        PaginationUtil paginationHelper = filter.createPaginationUtil(
            (int) accounts.getTotalElements(),
            24,
            5
        );

        model.addAttribute("accounts", accounts);
        model.addAttribute("resultCount" , accounts.getTotalElements());
        model.addAttribute("paginationHelper", paginationHelper);
        model.addAttribute("totalPages", accounts.getTotalPages());
        model.addAttribute("filter", filter);
        model.addAttribute("currentPage", "account-management");
        return "/screens/admin/accounts/index";
    }

    @PostMapping("/activate")
    public String activateAccount(
        @CurrentAccount Account account,
        @RequestBody AccountActivateForm accountActivateForm
    ) {

        accountService.toggleAccountActivation(accountActivateForm.getId(), accountActivateForm.isActivate());
        return "redirect:/admin/accounts";
    }
}
