package group1.intern.repository;

import group1.intern.model.ProductQuantity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductQuantitiesRepository extends JpaRepository<ProductQuantity, Integer> {
}
