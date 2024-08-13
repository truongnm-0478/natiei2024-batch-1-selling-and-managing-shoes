package group1.intern.bean;

import group1.intern.model.Embeddables.ProductDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductDetailColors {
    private Integer id;
    private Integer productId;
    private String color;
}
