package group1.intern.controller.customer;

import group1.intern.bean.PayForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PaymentsController {
    @GetMapping("/payments")
    public String showPaymentPage(Model model) {
        model.addAttribute("payForm", new PayForm());
        return "screens/payments/show";
    }

    @PostMapping("/payments")
    public String processPayment(@Valid @ModelAttribute("payForm") PayForm payForm, BindingResult bindingResult,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            return "screens/payments/show";
        }
        return "screens/payments/show";
    }
}
