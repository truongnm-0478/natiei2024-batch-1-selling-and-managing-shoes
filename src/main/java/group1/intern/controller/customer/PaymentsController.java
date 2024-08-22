package group1.intern.controller.customer;

import group1.intern.annotation.CurrentAccount;
import group1.intern.annotation.PreAuthorizeCustomer;
import group1.intern.bean.PayForm;
import group1.intern.bean.ShoppingCartWrapper;
import group1.intern.bean.ToastMessage;
import group1.intern.model.Account;
import group1.intern.service.PaymentsService;
import group1.intern.util.constant.CommonConstant;
import group1.intern.util.exception.BadRequestException;
import group1.intern.util.util.CommonUtils;
import group1.intern.util.util.WebUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
@PreAuthorizeCustomer
public class PaymentsController {
    private final PaymentsService paymentsService;

    @GetMapping
    public String showPaymentPage(@CurrentAccount Account currentAccount, Model model, RedirectAttributes redirectAttributes) {
        // If shopping cart is empty, back to cart pages
        var shoppingCartWrapper = WebUtils.Sessions.getAttribute(CommonConstant.SHOPPING_CART_WRAPPER, ShoppingCartWrapper.class);
        var totalPrice = WebUtils.Sessions.getAttribute(CommonConstant.TOTAL_PRICE, Integer.class);
        if (shoppingCartWrapper == null || CommonUtils.isEmptyOrNullList(shoppingCartWrapper.getShoppingCartInfos()) || totalPrice == null) {
            WebUtils.Sessions.removeAttribute(CommonConstant.SHOPPING_CART_WRAPPER);
            WebUtils.Sessions.removeAttribute(CommonConstant.TOTAL_PRICE);
            redirectAttributes.addFlashAttribute("toastMessages", new ToastMessage("error", "Giỏ hàng trống, bạn vui lòng thanh toán từ giỏ hàng!"));
            return "redirect:/carts";
        }

        model.addAttribute("payForm", PayForm.builder()
            .fullName(currentAccount.getFullName())
            .email(currentAccount.getEmail())
            .phoneNumber(currentAccount.getPhoneNumber())
            .address(currentAccount.getAddress())
            .build());
        model.addAttribute("currentAccount", currentAccount);
        model.addAttribute("shoppingCartWrapper", shoppingCartWrapper);
        model.addAttribute("totalPrice", totalPrice);
        return "screens/payments/new";
    }

    @PostMapping
    public String processPayment(
        @Valid @ModelAttribute("payForm") PayForm payForm,
        BindingResult bindingResult,
        @CurrentAccount Account currentAccount,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("payForm", payForm);
            return "screens/payments/new";
        }

        try {
            var shoppingCartWrapper = WebUtils.Sessions.getAttribute(CommonConstant.SHOPPING_CART_WRAPPER, ShoppingCartWrapper.class);
            var totalPrice = WebUtils.Sessions.getAttribute(CommonConstant.TOTAL_PRICE, Integer.class);
            if (shoppingCartWrapper != null && CommonUtils.isNotEmptyOrNullList(shoppingCartWrapper.getShoppingCartInfos()) && totalPrice != null) {
                paymentsService.processPayments(shoppingCartWrapper.getShoppingCartInfos(), payForm, currentAccount, totalPrice);
                redirectAttributes.addFlashAttribute("toastMessages", new ToastMessage("success", "Đặt hàng thành công!"));

                // Remove shopping cart wrapper in session
                WebUtils.Sessions.removeAttribute(CommonConstant.SHOPPING_CART_WRAPPER);
                WebUtils.Sessions.removeAttribute(CommonConstant.TOTAL_PRICE);
                return "redirect:/customer/orders?status=all"; // Redirect to order page
            } else
                throw new BadRequestException("Giỏ hàng trống, bạn vui lòng thanh toán từ giỏ hàng!");
        } catch (Exception e) {
            // Back to cart page if error
            redirectAttributes.addFlashAttribute("toastMessages", new ToastMessage("error", e.getMessage()));
            WebUtils.Sessions.removeAttribute(CommonConstant.SHOPPING_CART_WRAPPER);
            WebUtils.Sessions.removeAttribute(CommonConstant.TOTAL_PRICE);
            return "redirect:/carts";
        }
    }

    @GetMapping("/{quantityId}/{quantity}")
    public String showSinglePayment(
        @PathVariable("quantityId") int quantityId,
        @PathVariable("quantity") int quantity,
        @CurrentAccount Account currentAccount,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        model.addAttribute("payForm", PayForm.builder()
            .fullName(currentAccount.getFullName())
            .email(currentAccount.getEmail())
            .phoneNumber(currentAccount.getPhoneNumber())
            .address(currentAccount.getAddress())
            .build());
        model.addAttribute("currentAccount", currentAccount);

        try {
            // Get product info and calculate total price, then set to session
            var cart = paymentsService.singlePayment(quantityId, currentAccount, quantity);
            WebUtils.Sessions.setAttribute(cart);

            model.addAttribute("shoppingCartWrapper", cart.get(CommonConstant.SHOPPING_CART_WRAPPER));
            model.addAttribute("totalPrice", cart.get(CommonConstant.TOTAL_PRICE));
            return "screens/payments/new";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastMessages", new ToastMessage("error", e.getMessage()));
            return "redirect:/products";
        }
    }

}
