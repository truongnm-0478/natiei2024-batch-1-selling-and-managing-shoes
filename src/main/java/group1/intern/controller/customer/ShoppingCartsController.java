package group1.intern.controller.customer;

import group1.intern.annotation.CurrentAccount;
import group1.intern.bean.*;
import group1.intern.model.Account;
import group1.intern.service.ShoppingCartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


@Controller
@RequestMapping("/carts")
@SessionAttributes({"shoppingCartWrapper", "totalPrice"})
public class ShoppingCartsController {

    @Autowired
    private ShoppingCartsService shoppingCartsService;

    @ModelAttribute("totalPrice")
    public Integer initTotalPrice() {
        return 0;
    }

    @ModelAttribute("shoppingCartWrapper")
    public ShoppingCartWrapper initShoppingCartWrapper() {
        return new ShoppingCartWrapper();
    }

    @GetMapping
    public String index(
        Model model,
        @CurrentAccount Account account,
        @ModelAttribute("shoppingCartWrapper") ShoppingCartWrapper shoppingCartWrapper,
        @ModelAttribute("totalPrice") Integer totalPrice,
        RedirectAttributes redirectAttributes
    ) {
        if (account == null) {
            redirectAttributes.addFlashAttribute("toastMessages", new ToastMessage("error", "Bạn cần đăng nhập để thêm vào giỏ hàng !"));
            return "redirect:/login";
        }

        List<ShoppingCartInfo> shoppingCarts = shoppingCartsService.getShoppingCartsByCustomerId(account.getId());

        shoppingCartWrapper.setShoppingCartInfos(shoppingCarts);

        int totalOriginPrice = 0;
        int finalPrice = 0;
        int totalDiscountedPrice = 0;

        for (ShoppingCartInfo shoppingCart : shoppingCarts) {
            totalOriginPrice += shoppingCart.getPrice();
            finalPrice += shoppingCart.getPrice() * (100 - shoppingCart.getDiscount()) / 100;
            totalDiscountedPrice += shoppingCart.getPrice() * shoppingCart.getDiscount() / 100;
        }

        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        String totalOriginPriceFormatted =  numberFormat.format(totalOriginPrice) + " VND";
        String totalDiscountedPriceFormatted = numberFormat.format(totalDiscountedPrice) + " VND";
        String finalPriceFormatted = numberFormat.format(finalPrice) + " VND";

        totalPrice = finalPrice;

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
        @ModelAttribute("totalPrice") Integer totalPrice,
        @ModelAttribute("shoppingCartWrapper") ShoppingCartWrapper shoppingCartWrapper,
        Model model,
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
        shoppingCartWrapper.setShoppingCartInfos(shoppingCarts);

        redirectAttributes.addFlashAttribute("toastMessages", new ToastMessage("success", "Đã thêm " + cart.getQuantity() + " sản phẩm vào giỏ hàng !"));
        return "redirect:/carts";
    }
}
