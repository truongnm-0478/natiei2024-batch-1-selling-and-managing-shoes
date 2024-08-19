package group1.intern.bean;

import group1.intern.model.ProductQuantity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartForm {
    private ProductQuantity productQuantity;
    private int quantity;
}
