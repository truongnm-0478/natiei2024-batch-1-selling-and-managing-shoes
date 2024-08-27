package group1.intern.service.impl;

import group1.intern.bean.OrderInfo;
import group1.intern.bean.RevenueInfo;
import group1.intern.model.Account;
import group1.intern.model.Enum.AccountRole;
import group1.intern.model.Enum.OrderStatus;
import group1.intern.model.Order;
import group1.intern.repository.OrdersRepository;
import group1.intern.repository.customization.OrdersCustomRepository;
import group1.intern.service.OrdersService;
import group1.intern.util.exception.BadRequestException;
import group1.intern.util.exception.ForbiddenException;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {
    private final OrdersCustomRepository orderCustomRepository;
    private final OrdersRepository ordersRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<OrderInfo> getOrders(@Nullable Integer accountId, @Nullable OrderStatus status, Pageable pageable) {
        if (accountId != null) {
            var orders = status != null
                ? orderCustomRepository.findAllByAccountIdAndStatus(accountId, status, pageable)
                : orderCustomRepository.findAllByAccountId(accountId, pageable);
            return orders.map(OrderInfo::fromEntity);
        }
        return status != null
            ? orderCustomRepository.findAllByStatus(status, pageable).map(OrderInfo::fromEntity)
            : orderCustomRepository.findAllWithRelationship(pageable).map(OrderInfo::fromEntity);
    }

    @Override
    public void changeOrderStatus(Account account, Integer orderId, String status) {
        // Error messages
        final String noPermission = "Bạn không có quyền thực hiện hành động này!";
        final String notFound = "Không tìm thấy đơn hàng!";
        final String invalidStatus = "Trạng thái đơn hàng không hợp lệ!";

        // Check order status
        var orderStatus = OrderStatus.fromString(status);
        if (orderStatus == null)
            throw new BadRequestException(invalidStatus);

        // Check account permission
        if (account == null)
            throw new ForbiddenException(noPermission);
        // For customer, only allow to cancel or receive order
        if ((account.getRole() == AccountRole.CUSTOMER) && !(orderStatus == OrderStatus.CANCEL || orderStatus == OrderStatus.RECEIVED))
            throw new ForbiddenException(noPermission);
        // For seller, only allow to change order status to other than CANCEL and RECEIVED
        if ((account.getRole() == AccountRole.SELLER) && (orderStatus == OrderStatus.CANCEL || orderStatus == OrderStatus.RECEIVED))
            throw new ForbiddenException(noPermission);

        var order = ordersRepository.findById(orderId).orElseThrow(() -> new BadRequestException(notFound));
        order.setStatus(orderStatus);
        ordersRepository.save(order);
    }

    @Override
    public long countByDate(LocalDate date) {
        return ordersRepository.countByUpdatedAtBetween(
            date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }

    @Override
    public double sumTotalPriceByDate(LocalDate date) {
        Optional<Double> result = ordersRepository.sumTotalPriceByUpdatedAtBetweenAndStatus(
            date.atStartOfDay(), date.plusDays(1).atStartOfDay(), OrderStatus.DONE);
        return result.orElse(0.0);
    }

    @Override
    public double sumEstimatedRevenueByDate(LocalDate date) {
        Optional<Double> result = ordersRepository.sumTotalPriceByUpdatedAtBetweenAndStatuses(
            date.atStartOfDay(), date.plusDays(1).atStartOfDay(),
            Arrays.asList(OrderStatus.CONFIRM, OrderStatus.RECEIVED, OrderStatus.DONE));
        return result.orElse(0.0);
    }

    @Override
    public RevenueInfo getMonthlyRevenue(YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        long orderCount = ordersRepository.countByUpdatedAtBetween(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
        double totalRevenue = ordersRepository.sumTotalPriceByUpdatedAtBetweenAndStatus(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay(), OrderStatus.DONE).orElse(0.0);
        double predictedRevenue = ordersRepository.sumTotalPriceByUpdatedAtBetweenAndStatuses(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay(), Arrays.asList(OrderStatus.CONFIRM, OrderStatus.RECEIVED, OrderStatus.DONE)).orElse(0.0);

        return new RevenueInfo(orderCount, totalRevenue, predictedRevenue);
    }

    @Override
    public Map<String, RevenueInfo> getDailyRevenueDetails(YearMonth yearMonth) {
        Map<String, RevenueInfo> dailyRevenue = new LinkedHashMap<>();
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Order> orders = ordersRepository.findByUpdatedAtBetween(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDateTime dateTimeStart = date.atStartOfDay();
            LocalDateTime dateTimeEnd = date.plusDays(1).atStartOfDay();

            long orderCount = orders.stream().filter(o -> o.getUpdatedAt().isAfter(dateTimeStart) && o.getUpdatedAt().isBefore(dateTimeEnd)).count();
            double totalRevenue = orders.stream().filter(o -> o.getUpdatedAt().isAfter(dateTimeStart) && o.getUpdatedAt().isBefore(dateTimeEnd) && OrderStatus.DONE.equals(o.getStatus())).mapToDouble(Order::getTotalPrice).sum();
            double estimatedRevenue = orders.stream().filter(o -> o.getUpdatedAt().isAfter(dateTimeStart) && o.getUpdatedAt().isBefore(dateTimeEnd) && (OrderStatus.CONFIRM.equals(o.getStatus()) || OrderStatus.RECEIVED.equals(o.getStatus()) || OrderStatus.DONE.equals(o.getStatus()))).mapToDouble(Order::getTotalPrice).sum();

            dailyRevenue.put(date.toString(), new RevenueInfo(orderCount, totalRevenue, estimatedRevenue));
        }
        return dailyRevenue;
    }

    @Override
    public Map<String, RevenueInfo> getMonthlyRevenueDetails(int year) {
        Map<String, RevenueInfo> monthlyRevenue = new LinkedHashMap<>();

        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(year, month);
            long orderCount = 0;
            double totalRevenue = 0.0;
            double estimatedRevenue = 0.0;

            for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
                LocalDate date = yearMonth.atDay(day);
                orderCount += countByDate(date);
                totalRevenue += sumTotalPriceByDate(date);
                estimatedRevenue += sumEstimatedRevenueByDate(date);
            }
            monthlyRevenue.put(yearMonth.getMonth().toString(),
                new RevenueInfo(orderCount, totalRevenue, estimatedRevenue));
        }
        return monthlyRevenue;
    }

}