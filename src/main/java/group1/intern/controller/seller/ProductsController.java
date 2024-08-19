package group1.intern.controller.seller;

import group1.intern.annotation.PreAuthorizeSeller;
import group1.intern.bean.ProductDetailColors;
import group1.intern.bean.ProductDetailInfo;
import group1.intern.bean.ProductDetailInfoSeller;
import group1.intern.bean.ToastMessage;
import group1.intern.service.ProductService;
import group1.intern.util.exception.NotFoundObjectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller("sellerProductsController")
@PreAuthorizeSeller
@RequestMapping("/seller/products")
public class ProductsController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{id}")
    public String showProductDetailSeller(@PathVariable("id") Integer id, Model model) {
        try {
            ProductDetailInfoSeller productInfo = productService.getProductDetailByIdForManager(id);
            List<ProductDetailColors> productDetailColors = productService.getProductDetailColors(id);
            model.addAttribute("currentPage", "product-management");

            model.addAttribute("productInfo", productInfo);
            model.addAttribute("productDetailColors", productDetailColors);
            return "screens/seller/products/show";
        } catch (NotFoundObjectException ex) {
            model.addAttribute("toastMessages", new ToastMessage("error", ex.getMessage()));
            return "screens/products/index";
        }
    }

}
