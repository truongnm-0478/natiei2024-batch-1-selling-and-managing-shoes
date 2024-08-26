package group1.intern.controller.admin;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import group1.intern.bean.RevenueInfo;
import group1.intern.service.OrdersService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/statistic")
public class StatisticController {

    private final OrdersService ordersService;

    @GetMapping
    public String index(Model model, @RequestParam(name = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
    	if (yearMonth == null) {
            yearMonth = YearMonth.now();  // Mặc định là tháng/năm hiện tại nếu không có ngày được cung cấp
        }

    	RevenueInfo revenueInfo = ordersService.getMonthlyRevenue(yearMonth);
        Map<String, RevenueInfo> dailyRevenue = ordersService.getDailyRevenueDetails(yearMonth);
        Map<String, RevenueInfo> monthlyRevenue = ordersService.getMonthlyRevenueDetails(yearMonth.getYear());

        model.addAttribute("revenueInfo", revenueInfo);
        model.addAttribute("dailyRevenue", dailyRevenue);
        model.addAttribute("monthlyRevenue", monthlyRevenue);
        model.addAttribute("selectedDate", yearMonth);
        model.addAttribute("selectedYear", yearMonth.getYear());

        return "/screens/admin/statistic/index";
    }

    @GetMapping("/data")
    @ResponseBody
    public Map<String, Object> show(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        RevenueInfo revenueInfo = ordersService.getMonthlyRevenue(yearMonth);
        Map<String, RevenueInfo> dailyRevenue = ordersService.getDailyRevenueDetails(yearMonth);
        Map<String, RevenueInfo> monthlyRevenue = ordersService.getMonthlyRevenueDetails(yearMonth.getYear());

        Map<String, Object> response = new HashMap<>();
        response.put("revenueInfo", revenueInfo);
        response.put("dailyRevenue", dailyRevenue);
        response.put("monthlyRevenue", monthlyRevenue);

        return response;
    }
}