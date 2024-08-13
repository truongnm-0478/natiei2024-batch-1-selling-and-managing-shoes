package group1.intern.bean;

import group1.intern.model.ProductImage;
import group1.intern.model.ProductQuantity;
import group1.intern.model.ShoppingCart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingCartInfo {
    private Integer id;
    private Integer customerId;
    private Integer productId;
    private Integer productDetailId;
    private Integer productQuantityId;
    private int quantity;
    private String style;
    private int priceProduct;
    private int price;
    private String size;
    private String name;
    private Integer discount;
    private List<ProductImage> images;
    private ProductQuantity sizeQuantity;

    public static ShoppingCartInfo fromShoppingCart(ShoppingCart shoppingCart) {
        return ShoppingCartInfo.builder()
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
            .build();
    }
}
