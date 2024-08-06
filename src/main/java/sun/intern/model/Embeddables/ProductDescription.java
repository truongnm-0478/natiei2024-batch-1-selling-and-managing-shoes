package sun.intern.model.Embeddables;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDescription {
    private String text;
    private List<String> images;
}
