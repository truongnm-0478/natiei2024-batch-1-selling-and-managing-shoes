package group1.intern.repository;

import group1.intern.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartsRepository extends JpaRepository<ShoppingCart, Integer > {
}
