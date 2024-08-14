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

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

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

    public boolean isSoldOut() {
        return quantities.stream()
            .mapToInt(ProductQuantity::getQuantity)
            .sum() == 0;
    }

    public String getProductDetailUrl() {
        return "/products/" + this.id;
    }

    public String getOriginPriceFormated() {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return formatter.format(this.originPrice) + " VND";
    }

    public String getDiscountPriceFormated() {
        if (discount == null || discount <= 0) return "";
        int discountedPrice = originPrice - (originPrice * discount / 100);
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        return numberFormat.format(discountedPrice) + " VND";
    }

}