package group1.intern.repository;

import group1.intern.model.ShoppingCart;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ShoppingCartsRepository extends JpaRepository<ShoppingCart, Integer > {
    Optional<ShoppingCart> findShoppingCartByProductQuantityIdAndAccountId(Integer productQuantityId, Integer accountId);

    Optional<ShoppingCart> findShoppingCartByIdAndAccountId(Integer id, Integer accountId);

    @Modifying
    @Transactional
    @Query("UPDATE ShoppingCart sc set sc.productQuantity.id = ?2 where sc.id = ?1")
    void updateShoppingCartByProductQuantityID(Integer id, Integer productQuantityId);

    void deleteShoppingCartById(Integer id);

    void deleteAllByAccountId(Integer accountId);
}
