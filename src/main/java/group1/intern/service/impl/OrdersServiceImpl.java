package group1.intern.service.impl;

import group1.intern.bean.OrderInfo;
import group1.intern.model.Account;
import group1.intern.model.Enum.AccountRole;
import group1.intern.model.Enum.OrderStatus;
import group1.intern.repository.OrdersRepository;
import group1.intern.repository.customization.OrdersCustomRepository;
import group1.intern.service.OrdersService;
import group1.intern.util.exception.BadRequestException;
import group1.intern.util.exception.ForbiddenException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {
    private final OrdersCustomRepository orderCustomRepository;
    private final OrdersRepository ordersRepository;


    @Override
    public Page<OrderInfo> getOrders(@Nullable Integer accountId, @Nullable OrderStatus status, Pageable pageable) {
        if (accountId != null) {
            var orders = status != null
                ? orderCustomRepository.findAllByAccountIdAndStatus(accountId, status, pageable)
                : orderCustomRepository.findAllByAccountId(accountId, pageable);
            return orders.map(OrderInfo::fromEntity);
        }
        return null;
    }

    @Override
    public void changeOrderStatus(Account account, Integer orderId, String status) {
        // Error messages
        final String noPermission = "Bạn không có quyền thực hiện hành động này!";
        final String notFound = "Không tìm thấy đơn hàng!";
        final String invalidStatus = "Trạng thái đơn hàng không hợp lệ!";

        // Check order status
        var orderStatus = OrderStatus.fromString(status);
        if (orderStatus == null) throw new BadRequestException(invalidStatus);

        // Check account permission
        if (account == null) throw new ForbiddenException(noPermission);
        // For customer, only allow to cancel or receive order
        if (account.getRole() == AccountRole.CUSTOMER && !List.of(OrderStatus.CANCEL, OrderStatus.RECEIVED).contains(orderStatus))
            throw new ForbiddenException(noPermission);
        // For seller, only allow to change order status to other than CANCEL and RECEIVED
        if (account.getRole() == AccountRole.SELLER && !Arrays.stream(OrderStatus.values()).filter(e -> e != OrderStatus.CANCEL && e != OrderStatus.RECEIVED).toList().contains(orderStatus))
            throw new ForbiddenException(noPermission);

        var order = ordersRepository.findById(orderId).orElseThrow(() -> new BadRequestException(notFound));
        order.setStatus(orderStatus);
        ordersRepository.save(order);
    }
}
