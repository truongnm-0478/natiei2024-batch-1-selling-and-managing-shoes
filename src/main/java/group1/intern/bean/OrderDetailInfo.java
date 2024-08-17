package group1.intern.bean;

import group1.intern.model.OrderDetail;
import group1.intern.util.CurrencyUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailInfo {
    private Integer id;
    private String price;
    private String quantity;
    private String size;
    private ProductDetailInfo productDetailInfo;

    public static OrderDetailInfo fromEntity(OrderDetail orderDetail) {
        var productDetail = orderDetail.getProductQuantity().getProductDetail();
        return OrderDetailInfo.builder()
            .id(orderDetail.getId())
            .price(CurrencyUtil.formatCurrency(orderDetail.getPrice()))
            .quantity(String.valueOf(orderDetail.getQuantity()))
            .size(orderDetail.getProductQuantity().getSize().getValue())
            .productDetailInfo(
                ProductDetailInfo.builder()
                    .id(productDetail.getId())
                    .productId(productDetail.getProduct().getId())
                    .name(productDetail.getProduct().getName())
                    .discount(productDetail.getDiscount())
                    .gender(productDetail.getGender().toString())
                    .description(productDetail.getDescription())
                    .category(productDetail.getProduct().getCategory().getValue())
                    .style(productDetail.getStyle().getValue())
                    .material(productDetail.getProduct().getMaterial().getValue())
                    .price(CurrencyUtil.formatCurrency(productDetail.getPrice()))
                    .color(productDetail.getColor().getValue())
                    .images(productDetail.getImages())
                    // .sizeQuantity(productDetail.getQuantities()) // ignore this field
                    .build()
            )
            .build();
    }
}
