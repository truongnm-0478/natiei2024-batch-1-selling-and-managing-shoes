package group1.intern.repository.customization;

import group1.intern.model.ShoppingCart;
import group1.intern.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShoppingCartsCustomRepository extends CustomRepository<ShoppingCart, Integer, BaseRepository<ShoppingCart>> {
    Page<ShoppingCart> findAllByAccountId(Integer accountId, Pageable pageable);
}
