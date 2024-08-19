package group1.intern.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class ShoppingCartsController {

    @GetMapping("/carts")
    public ModelAndView index(
    ) {
        return new ModelAndView("screens/shopping-carts/index");
    }
}
