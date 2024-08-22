package group1.intern.controller.seller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import group1.intern.bean.RevenueInfo;
import group1.intern.repository.OrdersRepository;
import group1.intern.service.OrdersService;

@RestController
public class RevenuesController {
	@Autowired
    private OrdersService orderService;

    @GetMapping("/revenues/daily")
    @ResponseBody
    public RevenueInfo getDailyRevenue(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        // Tính số lượng đơn hàng trong ngày
        long orderCount = orderService.countByDate(date);

        // Tính doanh thu hiện tại từ các đơn hàng đã hoàn thành (status = 'DONE')
        double currentRevenue = orderService.sumTotalPriceByDate(date);

        // Tính doanh thu dự tính từ các đơn hàng đang chờ xác nhận (status = 'CONFIRM')
        double predictedRevenue = orderService.sumEstimatedRevenueByDate(date);

        // Tạo DTO trả về cho response
        RevenueInfo revenueDto = new RevenueInfo(orderCount, currentRevenue, predictedRevenue);
        return revenueDto;
    }
}
