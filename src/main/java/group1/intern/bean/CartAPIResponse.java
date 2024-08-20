package group1.intern.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartAPIResponse {
    private String message;
    private String totalOriginPrice;
    private String finalPrice;
    private String totalDiscountedPrice;
    private boolean reload;
}
