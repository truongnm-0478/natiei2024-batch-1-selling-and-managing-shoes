package group1.intern.controller.customer;

import group1.intern.annotation.CurrentAccount;
import group1.intern.bean.CartForm;
import group1.intern.bean.ShoppingCartInfo;
import group1.intern.bean.ShoppingCartWrapper;
import group1.intern.bean.ToastMessage;
import group1.intern.model.Account;
import group1.intern.service.ShoppingCartsService;
import group1.intern.util.constant.CommonConstant;
import group1.intern.util.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


@Controller
@RequestMapping("/carts")
@RequiredArgsConstructor
public class ShoppingCartsController {
    private final ShoppingCartsService shoppingCartsService;

    @GetMapping
    public String index(
        @RequestParam(value = "pay", required = false) Boolean pay,
        Model model,
        @CurrentAccount Account account,
        RedirectAttributes redirectAttributes
    ) {
        if (account == null) {
            redirectAttributes.addFlashAttribute("toastMessages", new ToastMessage("error", "Bạn cần đăng nhập để thêm vào giỏ hàng !"));
            return "redirect:/login";
        }

        List<ShoppingCartInfo> shoppingCarts = shoppingCartsService.getShoppingCartsByCustomerId(account.getId());
        int totalOriginPrice = 0;
        int finalPrice = 0;
        int totalDiscountedPrice = 0;

        for (ShoppingCartInfo shoppingCart : shoppingCarts) {
            totalOriginPrice += shoppingCart.getPrice();
            finalPrice += shoppingCart.getPrice() * (100 - shoppingCart.getDiscount()) / 100;
            totalDiscountedPrice += shoppingCart.getPrice() * shoppingCart.getDiscount() / 100;
        }

        // Set shopping cart infos and total price to session
        if (pay != null && pay) {
            WebUtils.Sessions.setAttribute(CommonConstant.SHOPPING_CART_WRAPPER, new ShoppingCartWrapper(shoppingCarts));
            WebUtils.Sessions.setAttribute(CommonConstant.TOTAL_PRICE, finalPrice);
            return "redirect:/payments";
        }

        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        String totalOriginPriceFormatted = numberFormat.format(totalOriginPrice) + " VND";
        String totalDiscountedPriceFormatted = numberFormat.format(totalDiscountedPrice) + " VND";
        String finalPriceFormatted = numberFormat.format(finalPrice) + " VND";

        model.addAttribute("shoppingCarts", shoppingCarts);
        model.addAttribute("totalOriginPrice", totalOriginPriceFormatted);
        model.addAttribute("totalDiscountPrice", totalDiscountedPriceFormatted);
        model.addAttribute("finalPrice", finalPriceFormatted);
        model.addAttribute("shoppingCarts", shoppingCarts);
        model.addAttribute("cartForm", new CartForm());

        return "screens/shopping-carts/index";
    }

    @PostMapping
    public String create(
        @ModelAttribute CartForm cart,
        @CurrentAccount Account account,
        RedirectAttributes redirectAttributes
    ) {
        if (account == null) {
            redirectAttributes.addFlashAttribute("toastMessages", new ToastMessage("error", "Bạn cần đăng nhập để thêm vào giỏ hàng !"));
            return "redirect:/login";
        }

        if (cart.getProductQuantity() == null || cart.getQuantity() <= 0) {
            redirectAttributes.addFlashAttribute("toastMessages", new ToastMessage("error", "Vui lòng chọn size và số lượng !"));
            return "redirect:/carts";
        }

        shoppingCartsService.addProductToCart(account, cart.getProductQuantity(), cart.getQuantity());

        List<ShoppingCartInfo> shoppingCarts = shoppingCartsService.getShoppingCartsByCustomerId(account.getId());
        redirectAttributes.addFlashAttribute("toastMessages", new ToastMessage("success", "Đã thêm " + cart.getQuantity() + " sản phẩm vào giỏ hàng !"));
        return "redirect:/carts";
    }
}
