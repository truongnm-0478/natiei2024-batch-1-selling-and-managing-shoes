package sun.intern.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "constants")
public class Constant extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private String value;

    @OneToMany(mappedBy = "style",fetch = FetchType.LAZY)
    private List<Product> styleProducts;

    @OneToMany(mappedBy = "material",fetch = FetchType.LAZY)
    private List<Product> materialProducts;

    @OneToMany(mappedBy = "color",fetch = FetchType.LAZY)
    private List<ProductDetail> colorProducts;

    @OneToMany(mappedBy = "size",fetch = FetchType.LAZY)
    private List<ProductDetail> sizeProducts;
}
