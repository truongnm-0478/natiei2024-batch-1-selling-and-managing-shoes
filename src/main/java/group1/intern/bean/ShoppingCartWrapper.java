package group1.intern.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartWrapper {
    private List<ShoppingCartInfo> shoppingCartInfos;
}
