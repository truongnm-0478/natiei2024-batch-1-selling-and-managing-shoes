package group1.intern.controller;

import group1.intern.bean.ProductDetailColors;
import group1.intern.bean.ProductDetailInfo;
import group1.intern.bean.ToastMessage;
import group1.intern.model.Product;
import group1.intern.service.ProductService;
import group1.intern.util.exception.NotFoundObjectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products/{id}")
    public String showRegisterPage(@PathVariable("id") Integer id, Model model) {
        try {
            ProductDetailInfo productInfo = productService.getProductDetailById(id);
            List<ProductDetailColors> productDetailColors = productService.getProductDetailColors(id);
            model.addAttribute("productInfo", productInfo);
            model.addAttribute("productDetailInfoList", productDetailColors);
            return "screens/products/show";
        } catch (NotFoundObjectException ex) {
            model.addAttribute("toastMessages", new ToastMessage("error", ex.getMessage()));
            return "screens/products/index";
        }
    }
}
