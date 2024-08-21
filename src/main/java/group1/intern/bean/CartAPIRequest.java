package group1.intern.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartAPIRequest {
    private Integer productQuantityId;
    private Integer quantity;
    // action: "size-change" / "quantity-change"
    private String action;
}
