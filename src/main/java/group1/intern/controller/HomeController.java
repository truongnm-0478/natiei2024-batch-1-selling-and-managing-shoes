package group1.intern.controller;

import group1.intern.bean.ToastMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    @GetMapping("/")
    public String homepage(Model model) {
        return "index";
    }
}
