package group1.intern.service;

import group1.intern.bean.OrderInfo;
import group1.intern.model.Account;
import group1.intern.model.Enum.OrderStatus;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrdersService {
    Page<OrderInfo> getOrders(@Nullable Integer accountId, @Nullable OrderStatus status, Pageable pageable);

    void changeOrderStatus(Account account, Integer orderId, String status);
}
