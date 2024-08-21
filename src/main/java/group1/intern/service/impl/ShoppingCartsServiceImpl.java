package group1.intern.service.impl;

import group1.intern.bean.ShoppingCartInfo;
import group1.intern.model.Account;
import group1.intern.model.ProductQuantity;
import group1.intern.model.ShoppingCart;
import group1.intern.repository.ShoppingCartsRepository;
import group1.intern.repository.base.ShoppingCartsBaseRepository;
import group1.intern.repository.base.WhereClauseType;
import group1.intern.repository.base.WhereElements;
import group1.intern.repository.customization.ShoppingCartsCustomRepository;
import group1.intern.service.ShoppingCartsService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShoppingCartsServiceImpl implements ShoppingCartsService {

    @Autowired
    private ShoppingCartsCustomRepository repository;
    @Autowired
    private ShoppingCartsRepository shoppingCartsRepository;
    @Autowired
    private ShoppingCartsBaseRepository baseRepository;

    @Override
    @Transactional
    public List<ShoppingCartInfo> getShoppingCartsByCustomerId(Integer customerId) {
        List<WhereElements> whereElements = new ArrayList<>();
        whereElements.add(WhereElements.builder()
            .key("account.id")
            .value(customerId)
            .type(WhereClauseType.EQUAL)
            .build());

        List<ShoppingCart> shoppingCarts = baseRepository.fetchAllDataWithoutPagination(whereElements, Sort.by("id"));

        if (!shoppingCarts.isEmpty()) {
            for (ShoppingCart cart : shoppingCarts) {
                if (cart.getProductQuantity() != null && cart.getProductQuantity().getProductDetail() != null) {
                    // call to trigger lazy fetch
                    cart.getProductQuantity().getProductDetail().getQuantities().size();
                }
            }
        }


        return shoppingCarts.stream().map(shoppingCart -> ShoppingCartInfo.builder()
                .id(shoppingCart.getId())
                .customerId(shoppingCart.getAccount().getId())
                .productId(shoppingCart.getProductQuantity().getProductDetail().getProduct().getId())
                .productDetailId(shoppingCart.getProductQuantity().getProductDetail().getId())
                .productQuantityId(shoppingCart.getProductQuantity().getId())
                .quantity(shoppingCart.getQuantity())
                .style(shoppingCart.getProductQuantity().getProductDetail().getStyle().getValue())
                .priceProduct(shoppingCart.getProductQuantity().getProductDetail().getPrice())
                .price(shoppingCart.getQuantity() * shoppingCart.getProductQuantity().getProductDetail().getPrice())
                .size(shoppingCart.getProductQuantity().getSize().getValue())
                .name(shoppingCart.getProductQuantity().getProductDetail().getProduct().getName())
                .discount(shoppingCart.getProductQuantity().getProductDetail().getDiscount())
                .images(shoppingCart.getProductQuantity().getProductDetail().getImages())
                .sizeQuantity(shoppingCart.getProductQuantity())
                .quantities(shoppingCart.getProductQuantity().getProductDetail().getQuantities())
                .build()
//
            )
            .collect(Collectors.toList());
    }

    @Override
    public Page<ShoppingCartInfo> getShoppingCartsByCustomerId(Integer customerId, Pageable pageable) {
        var shoppingCarts = repository.findAllByAccountId(customerId, pageable);
        return shoppingCarts.map(ShoppingCartInfo::fromShoppingCart);
    }

    @Override
    public void addProductToCart(Account account, ProductQuantity productQuantity, int quantity) {

        Optional<ShoppingCart>  oldShoppingCart = shoppingCartsRepository.findShoppingCartByProductQuantityIdAndAccountId(productQuantity.getId(), account.getId());

        if (oldShoppingCart.isPresent()) {
            oldShoppingCart.get().setQuantity(oldShoppingCart.get().getQuantity() + quantity);
            shoppingCartsRepository.save(oldShoppingCart.get());
            return;
        }

        ShoppingCart shoppingCart = ShoppingCart.builder()
            .account(account)
            .quantity(quantity)
            .productQuantity(productQuantity)
            .build();

        shoppingCartsRepository.save(shoppingCart);
    }

    @Override
    public String updateProductInCart(int cartId, int accountId, int quantityId, int quantity, String action) {
        ShoppingCart cart = shoppingCartsRepository.findById(cartId).orElse(null);

        if (cart == null) {
            throw new IllegalArgumentException("cart not found");
        }

        String isNeedUpdate = "no-update";

        switch(action) {
            // find if update size is already in cart
            // 1. if in cart -> delete old cart item -> update new cart item with new size ID and quantity
            // 2. if not in cart -> update cart item with new size ID and quantity
            case "size-change":
                Optional<ShoppingCart> oldCart = shoppingCartsRepository.findShoppingCartByProductQuantityIdAndAccountId(quantityId, accountId);
                if (oldCart.isPresent()) {
                    shoppingCartsRepository.delete(oldCart.get());
                    isNeedUpdate = "update";
                }
                shoppingCartsRepository.updateShoppingCartByProductQuantityID(cartId, quantityId);
                break;
            case "quantity-change":
                cart.setQuantity(quantity);
                shoppingCartsRepository.save(cart);
                break;
            default:
                throw new IllegalArgumentException("only support size-change and quantity-change action");
        }

        return isNeedUpdate;
    }
}
