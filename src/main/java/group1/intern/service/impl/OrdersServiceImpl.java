package group1.intern.service.impl;

import group1.intern.bean.OrderInfo;
import group1.intern.model.Enum.OrderStatus;
import group1.intern.repository.customization.OrdersCustomRepository;
import group1.intern.service.OrdersService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {
    private final OrdersCustomRepository orderCustomRepository;

    @Override
    public Page<OrderInfo> getOrders(@Nullable Integer accountId, @Nullable OrderStatus status, Pageable pageable) {
        if (accountId != null) {
            var orders = status != null
                ? orderCustomRepository.findAllByAccount_IdAndStatus(accountId, status, pageable)
                : orderCustomRepository.findAllByAccount_Id(accountId, pageable);
            return orders.map(OrderInfo::fromEntity);
        }
        return null;
    }
}
