package group1.intern.bean;

import group1.intern.model.Embeddables.ProductDescription;
import group1.intern.model.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductInfo {
    public Integer id;
    public String name;
    private String size;
    private int quantity;
    private Integer discount;
    private String gender;
    private ProductDescription description;
    private String category;
    private String style;
    private String material;
    private String price;
    private List<String> colors;
    private List<ProductImage> images;
    private Map<String, Integer> sizeQuantity;

}

