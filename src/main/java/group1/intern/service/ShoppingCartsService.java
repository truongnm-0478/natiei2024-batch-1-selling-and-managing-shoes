package group1.intern.service;

import group1.intern.bean.ShoppingCartInfo;
import group1.intern.model.Account;
import group1.intern.model.ProductQuantity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShoppingCartsService {
    List<ShoppingCartInfo> getShoppingCartsByCustomerId(Integer customerId);

    Page<ShoppingCartInfo> getShoppingCartsByCustomerId(Integer customerId, Pageable pageable);

    String updateProductInCart(int cartId, int accountId, int quantityId, int quantity, String action);

    void addProductToCart(Account account, ProductQuantity productQuantity, int quantity);

    @Transactional
    void deleteCartItemByID(int cartId, int accountId);

    @Transactional
    void deleteAllCartItemsByAccountId(int accountId);
}
