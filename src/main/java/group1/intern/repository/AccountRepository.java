package group1.intern.repository;

import group1.intern.model.Account;
import group1.intern.model.Enum.AccountRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT a FROM Account a WHERE " +
        "(LOWER(a.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
        "LOWER(a.displayName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
        "LOWER(a.email) LIKE LOWER(CONCAT('%', :query, '%'))) " +
        "AND (:role IS NULL OR a.role IN :role)")
    Page<Account> findByFilter(String query, List<AccountRole> role, Pageable pageable);
}
