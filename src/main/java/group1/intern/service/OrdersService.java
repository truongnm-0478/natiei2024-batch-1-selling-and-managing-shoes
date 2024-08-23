package group1.intern.service;

import group1.intern.bean.OrderInfo;
import group1.intern.bean.RevenueInfo;
import group1.intern.model.Account;
import group1.intern.model.Enum.OrderStatus;
import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrdersService {
    Page<OrderInfo> getOrders(@Nullable Integer accountId, @Nullable OrderStatus status, Pageable pageable);

    void changeOrderStatus(Account account, Integer orderId, String status);
    long countByDate(LocalDate date);
    double sumTotalPriceByDate(LocalDate date);
    double sumEstimatedRevenueByDate(LocalDate date);
    RevenueInfo getMonthlyRevenue(YearMonth yearMonth);
    Map<String, RevenueInfo> getDailyRevenueDetails(YearMonth yearMonth);
    Map<String, RevenueInfo> getMonthlyRevenueDetails(int year);
}