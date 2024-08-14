package group1.intern.repository;

import group1.intern.model.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductDetail, Integer> {
}