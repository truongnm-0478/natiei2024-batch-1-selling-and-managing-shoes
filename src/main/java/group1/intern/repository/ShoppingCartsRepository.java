package group1.intern.repository;

import group1.intern.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShoppingCartsRepository extends JpaRepository<ShoppingCart, Integer > {

    Optional<ShoppingCart> findShoppingCartByProductQuantityIdAndAccountId(Integer productQuantityId, Integer accountId);
}
