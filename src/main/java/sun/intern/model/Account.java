package sun.intern.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import sun.intern.model.Enum.AccountRole;
import sun.intern.model.RefreshToken;
import java.time.LocalDate;
import java.util.List;


@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "accounts")
public class Account extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String displayName;
    private String password;
    @Enumerated(EnumType.STRING)
    private AccountRole role;
    private String fullName;
    private LocalDate dateOfBirth;
    private String address;
    private String phoneNumber;
    private Boolean gender;
    private String avatar;
    private Boolean isActive = true;
    // One-to-Many relationship with RefreshToken
    @OneToMany(mappedBy = "account",fetch = FetchType.LAZY)
    private List<RefreshToken> refreshTokens;
    // One-to-Many relationship with ShoppingCart
    @OneToMany(mappedBy = "account",fetch = FetchType.LAZY)
    private List<ShoppingCart> shoppingCarts;
    // One-to-Many relationship with Order
    @OneToMany(mappedBy = "account",fetch = FetchType.LAZY)
    private List<Order> orders;
}

