package group1.intern.service.impl;

import group1.intern.model.Account;
import group1.intern.model.Enum.AccountRole;
import group1.intern.repository.AccountRepository;
import group1.intern.service.AccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class AccountsServiceImpl implements AccountsService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountsServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Page<Account> findAccountsByFilter(int page, int size, String order, String role, String sortBy, String query) {
        Pageable pageable = PageRequest.of(page - 1, size, order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);

        List<AccountRole> roles;
        if ("customer".equalsIgnoreCase(role)) {
            roles = Collections.singletonList(AccountRole.CUSTOMER);
        } else if ("manager".equalsIgnoreCase(role)) {
            roles = Arrays.asList(AccountRole.ADMIN, AccountRole.SELLER);
        } else {
            roles = Arrays.asList(AccountRole.values());
        }

        return accountRepository.findByFilter(query, roles, pageable);
    }

    @Override
    public void toggleAccountActivation(int accountId, boolean active) {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account != null) {
            account.setIsActivated(active);
            accountRepository.save(account);
        }
    }
}
