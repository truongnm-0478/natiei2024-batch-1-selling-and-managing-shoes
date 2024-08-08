package group1.intern.util.seeder;

import group1.intern.model.Account;
import group1.intern.model.Enum.AccountRole;
import group1.intern.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@RequiredArgsConstructor
@Slf4j
@Component
@Order(1)
public class AccountSeeder implements CommandLineRunner {
    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        final String passwordHash = passwordEncoder.encode("12345678");
        // Customer account
        var customerEmail = "customer@gmail.com";
        if (!repository.existsByEmail(customerEmail)) {
            var customerAccount = Account.builder()
                .email(customerEmail)
                .address("Viet Nam")
                .displayName("Customer")
                .fullName("Customer")
                .gender(true)
                .isActivated(true)
                .password(passwordHash)
                .phoneNumber("0123456789")
                .role(AccountRole.CUSTOMER)
                .dateOfBirth(LocalDate.of(2003, 1, 1))
                .build();
            try {
                repository.save(customerAccount);
            } catch (Exception e) {
                log.error("Error seeding customer account: {}", e.getMessage());
            }
        }

        // Seller account
        var sellerEmail = "seller@gmail.com";
        if (!repository.existsByEmail(sellerEmail)) {
            var sellerAccount = Account.builder()
                .email(sellerEmail)
                .address("Viet Nam")
                .displayName("Seller")
                .fullName("Seller")
                .gender(true)
                .isActivated(true)
                .password(passwordHash)
                .phoneNumber("0123456789")
                .role(AccountRole.SELLER)
                .dateOfBirth(LocalDate.of(2003, 1, 1))
                .build();
            try {
                repository.save(sellerAccount);
            } catch (Exception e) {
                log.error("Error seeding seller account: {}", e.getMessage());
            }
        }

        // Admin account
        var adminEmail = "admin@gmail.com";
        if (!repository.existsByEmail(adminEmail)) {
            var adminAccount = Account.builder()
                .email(adminEmail)
                .address("Viet Nam")
                .displayName("Admin")
                .fullName("Admin")
                .gender(true)
                .isActivated(true)
                .password(passwordHash)
                .phoneNumber("0123456789")
                .role(AccountRole.ADMIN)
                .dateOfBirth(LocalDate.of(2003, 1, 1))
                .build();
            try {
                repository.save(adminAccount);
            } catch (Exception e) {
                log.error("Error seeding admin account: {}", e.getMessage());
            }
        }
    }
}
