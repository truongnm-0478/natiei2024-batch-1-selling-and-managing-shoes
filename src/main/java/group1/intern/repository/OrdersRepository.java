package group1.intern.repository;

import group1.intern.bean.OrderInfo;
import group1.intern.bean.RevenueInfo;
import group1.intern.model.Order;
import group1.intern.model.Enum.OrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;

public interface OrdersRepository extends JpaRepository<Order, Integer> {

	long countByUpdatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.updatedAt BETWEEN :start AND :end AND o.status = :status")
    Optional<Double> sumTotalPriceByUpdatedAtBetweenAndStatus(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("status") OrderStatus status);

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.updatedAt BETWEEN :start AND :end AND o.status IN :statuses")
    Optional<Double> sumTotalPriceByUpdatedAtBetweenAndStatuses(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("statuses") List<OrderStatus> statuses);

	List<Order> findByUpdatedAtBetween(LocalDateTime start, LocalDateTime end);
}
