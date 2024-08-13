package group1.intern.model;

import group1.intern.model.Embeddables.ProductDescription;
import group1.intern.model.Enum.ProductGender;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_details")
public class ProductDetail extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private int originPrice;
    private Integer discount;
    private int price;
    @Enumerated(EnumType.STRING)
    private ProductGender gender;
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private ProductDescription description;
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "productDetail", fetch = FetchType.EAGER)
    private List<ProductImage> images;

    @OneToMany(mappedBy = "productDetail", fetch = FetchType.LAZY)
    private List<ProductQuantity> quantities;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "color_id")
    private Constant color;

    @ManyToOne
    @JoinColumn(name = "style_id")
    private Constant style;
}
