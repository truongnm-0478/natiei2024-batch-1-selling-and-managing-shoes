package group1.intern.controller;

import group1.intern.bean.AccountInfo;
import group1.intern.util.constant.CommonConstant;
import group1.intern.util.util.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String homepage() {
        var currentAccount = WebUtils.Sessions.getAttribute(CommonConstant.CURRENT_USER, AccountInfo.class);
        if (currentAccount != null && currentAccount.getRole() != 1) {
            return "redirect:/admin/statistic";
        }
        return "index";
    }
}
