package group1.intern.controller.seller;

import group1.intern.annotation.PreAuthorizeSeller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("sellerProductsController")
@PreAuthorizeSeller
@RequestMapping("/seller")
public class ProductsController {
    @GetMapping("/products/{id}")
    public String showProductDetailSeller(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("currentPage", "product-management");
        return "screens/seller/products/show";
    }
}
