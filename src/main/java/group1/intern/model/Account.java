package group1.intern.model;

import group1.intern.model.Enum.AccountRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "accounts")
public class Account extends EntityBase implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
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
    private String avatarUrl;
    private Boolean isActivated = true;
    // One-to-Many relationship with RefreshToken
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<RefreshToken> refreshTokens;
    // One-to-Many relationship with ShoppingCart
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<ShoppingCart> shoppingCarts;
    // One-to-Many relationship with Order
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Order> orders;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActivated;
    }
}

