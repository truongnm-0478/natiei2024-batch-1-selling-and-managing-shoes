package group1.intern.controller.admin;

import group1.intern.annotation.CurrentAccount;
import group1.intern.annotation.PreAuthorizeAdmin;
import group1.intern.bean.AccountActivateForm;
import group1.intern.bean.AccountFilter;
import group1.intern.bean.AccountRegistration;
import group1.intern.bean.ToastMessage;
import group1.intern.model.Account;
import group1.intern.model.Enum.AccountRole;
import group1.intern.service.AccountsService;
import group1.intern.service.AuthService;
import group1.intern.util.util.PaginationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminAccountsController")
@PreAuthorizeAdmin
@RequestMapping("/admin/accounts")
@RequiredArgsConstructor
public class AccountsController {
    private final AccountsService accountService;
    private final AuthService authService;

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
        model.addAttribute("resultCount", accounts.getTotalElements());
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

    @GetMapping("/new")
    public String showSellerAccountForm(Model model) {
        model.addAttribute("sellerAccount", new AccountRegistration());
        return "/screens/admin/accounts/new";
    }

    @PostMapping
    public String createSellerAccount(
        @Valid @ModelAttribute("sellerAccount") AccountRegistration sellerAccount,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttrs
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("sellerAccount", sellerAccount);
            return "/screens/admin/accounts/new";
        }

        try {
            authService.register(sellerAccount, AccountRole.SELLER);
        } catch (Exception ex) {
            model.addAttribute("toastMessages", new ToastMessage("error", ex.getMessage()));
            return "/screens/admin/accounts/new";
        }

        redirectAttrs.addFlashAttribute("toastMessages", new ToastMessage("success", "Tạo tài khoản seller thành công"));
        return "redirect:/admin/accounts";
    }
}
