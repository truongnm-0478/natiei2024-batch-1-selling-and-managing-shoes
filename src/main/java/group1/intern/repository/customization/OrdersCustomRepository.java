package group1.intern.repository.customization;

import group1.intern.model.Enum.OrderStatus;
import group1.intern.model.Order;
import group1.intern.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrdersCustomRepository extends CustomRepository<Order, Integer, BaseRepository<Order>> {
    Page<Order> findAllByAccount_Id(Integer accountId, Pageable pageable);

    Page<Order> findAllByAccount_IdAndStatus(Integer accountId, OrderStatus status, Pageable pageable);
}
