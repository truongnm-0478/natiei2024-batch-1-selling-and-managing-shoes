package group1.intern.service;

import group1.intern.bean.ShoppingCartInfo;
import group1.intern.model.Account;
import group1.intern.model.ProductQuantity;
import group1.intern.model.ShoppingCart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShoppingCartsService {
    List<ShoppingCartInfo> getShoppingCartsByCustomerId(Integer customerId);

    Page<ShoppingCartInfo> getShoppingCartsByCustomerId(Integer customerId, Pageable pageable);

    void addProductToCart(Account account, ProductQuantity productQuantity, int quantity);
}
