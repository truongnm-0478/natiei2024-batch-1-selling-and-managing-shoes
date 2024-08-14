package group1.intern.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ProductsController {
    @GetMapping("/products")
    public String index() {
        return "screens/products/index";
    }
    
}
