package group1.intern.controller;

import group1.intern.bean.AccountRegistration;
import group1.intern.service.AuthService;
import group1.intern.util.exception.BadRequestException;
import group1.intern.util.exception.DuplicateEmailException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class RegisterController {

    @Autowired
    private AuthService authService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("accountRegistration", new AccountRegistration());
        return "screens/auth/register";
    }

    @PostMapping("/register")
    public String registerAccount(
        @Valid @ModelAttribute("accountRegistration") AccountRegistration accountRegistration,
        BindingResult bindingResult,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "screens/auth/register";
        }

        try {
            authService.register(accountRegistration);
        } catch (DuplicateEmailException | BadRequestException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "screens/auth/register";
        }

        model.addAttribute("message", "Đăng kí thành công");
        return "redirect:/login";
    }

}