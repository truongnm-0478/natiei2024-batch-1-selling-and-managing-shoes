package group1.intern.bean;

import group1.intern.model.ProductImage;
import group1.intern.model.ProductQuantity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailEdit {
    private Integer id;
    private Integer productId;
    private String name;
    private Integer discount;
    private String gender;
    private String category;
    private String style;
    private String material;
    private Integer price;
    private Integer originPrice;
    private String color;
    private List<ProductQuantity> sizeQuantity;

}
