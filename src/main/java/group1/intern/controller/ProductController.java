package group1.intern.controller;

import group1.intern.bean.ProductInfo;
import group1.intern.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products/{id}")
    public String showRegisterPage(@PathVariable("id") Integer id, Model model) {
        ProductInfo productInfo = productService.getProductById(id);
        System.out.println("productInfo = " + productInfo);

        model.addAttribute("productInfo", productInfo);
        return "screens/products/show";
    }
}
