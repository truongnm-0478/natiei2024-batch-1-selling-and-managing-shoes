package group1.intern.controller.customer;

import group1.intern.annotation.CurrentAccount;
import group1.intern.bean.PayForm;
import group1.intern.bean.ShoppingCartInfo;
import group1.intern.bean.ShoppingCartWrapper;
import group1.intern.model.Account;
import group1.intern.service.PaymentsService;
import group1.intern.util.util.WebUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/payments")
@Controller
@RequiredArgsConstructor
public class PaymentsController {

    @Autowired
    private PaymentsService paymentsService;

    @GetMapping
    public String showPaymentPage(
        @CurrentAccount Account currentAccount,
        Model model) {
        model.addAttribute("payForm", PayForm.builder()
            .fullName(currentAccount.getFullName())
            .email(currentAccount.getEmail())
            .phoneNumber(currentAccount.getPhoneNumber())
            .address(currentAccount.getAddress())
            .build());
        model.addAttribute("currentAccount", currentAccount);
        return "screens/payments/new";
    }

    @PostMapping
    public String processPayment(@Valid @ModelAttribute("payForm") PayForm payForm,
                                 BindingResult bindingResult,
                                 @CurrentAccount Account currentAccount,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("payForm", payForm);
            return "screens/payments/new";
        }
        List<ShoppingCartInfo> shoppingCartInfos = WebUtils.Sessions.getAttribute("shoppingCartWrapper", ShoppingCartWrapper.class).getShoppingCartInfos();
        paymentsService.processPayments(
            shoppingCartInfos,
            payForm,
            currentAccount,
            WebUtils.Sessions.getAttribute("totalPrice", Integer.class)
        );
        WebUtils.Sessions.removeAttribute("shoppingCartWrapper");
        WebUtils.Sessions.removeAttribute("totalPrice");
        return "redirect:/carts";
    }

    @GetMapping("/{quatityId}/{quantity}")
    public String showSinglePayment(@PathVariable("quatityId") int quantityId,
                                    @PathVariable("quantity") int quantity,
                                    @CurrentAccount Account currentAccount,
                                    Model model) {
        model.addAttribute("payForm", PayForm.builder()
            .fullName(currentAccount.getFullName())
            .email(currentAccount.getEmail())
            .phoneNumber(currentAccount.getPhoneNumber())
            .address(currentAccount.getAddress())
            .build());
        model.addAttribute("currentAccount", currentAccount);
        paymentsService.singlePayment(quantityId, currentAccount, quantity);
        return "screens/payments/new";
    }

}
