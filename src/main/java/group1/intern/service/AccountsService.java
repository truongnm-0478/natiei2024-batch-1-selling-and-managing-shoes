package group1.intern.service;

import group1.intern.model.Account;
import org.springframework.data.domain.Page;

public interface AccountsService {
    Page<Account> findAccountsByFilter(int page, int size, String order, String role, String sortBy, String query);

    void toggleAccountActivation(int accountId, boolean active);
}
