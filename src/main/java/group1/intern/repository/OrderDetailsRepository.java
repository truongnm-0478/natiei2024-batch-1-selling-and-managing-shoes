package group1.intern.repository;

import group1.intern.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailsRepository extends JpaRepository<OrderDetail, Integer> {
}
