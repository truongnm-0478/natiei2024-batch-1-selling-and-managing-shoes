package group1.intern.repository;

import group1.intern.model.Account;
import group1.intern.model.Enum.AccountRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        // Initialize database with test data
        Account adminAccount = Account.builder()
            .email("admin@gmail.com")
            .role(AccountRole.ADMIN)
            .fullName("Admin")
            .isActivated(true)
            .build();

        Account customerAccount1 = Account.builder()
            .email("customer1@gmail.com")
            .role(AccountRole.CUSTOMER)
            .fullName("Customer 1")
            .isActivated(true)
            .build();

        Account customerAccount2 = Account.builder()
            .email("customer2@gmail.com")
            .role(AccountRole.CUSTOMER)
            .fullName("Customer 2")
            .isActivated(true)
            .build();

        Account sellerAccount = Account.builder()
            .email("seller@gmail.com")
            .role(AccountRole.SELLER)
            .fullName("Seller")
            .isActivated(true)
            .build();

        accountRepository.saveAll(List.of(adminAccount, customerAccount1, customerAccount2, sellerAccount));
    }

    @Test
    void testFindByFilter_AdminEmail() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Account> result = accountRepository.findByFilter("admin@gmail.com", List.of(AccountRole.ADMIN, AccountRole.CUSTOMER, AccountRole.SELLER), pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("admin@gmail.com", result.getContent().get(0).getEmail());
    }

    @Test
    void testFindByFilter_ManagerRole() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Account> result = accountRepository.findByFilter("", List.of(AccountRole.ADMIN, AccountRole.SELLER), pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getContent().stream().filter(account -> account.getRole().equals(AccountRole.CUSTOMER)).count());
    }

    @Test
    void testFindByFilter_CustomerName() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Account> result = accountRepository.findByFilter("Customer", List.of(AccountRole.CUSTOMER), pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().stream().filter(account -> account.getFullName().contains("Customer")).count());
    }

}
