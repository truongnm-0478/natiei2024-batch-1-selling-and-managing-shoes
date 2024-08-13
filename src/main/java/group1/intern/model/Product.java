package group1.intern.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Constant category;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private Constant material;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductDetail> productDetails;
}
