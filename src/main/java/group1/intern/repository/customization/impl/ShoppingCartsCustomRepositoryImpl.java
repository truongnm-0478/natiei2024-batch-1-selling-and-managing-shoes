package group1.intern.repository.customization.impl;

import group1.intern.model.ShoppingCart;
import group1.intern.repository.base.BaseRepository;
import group1.intern.repository.base.WhereClauseType;
import group1.intern.repository.base.WhereElements;
import group1.intern.repository.customization.ShoppingCartsCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ShoppingCartsCustomRepositoryImpl implements ShoppingCartsCustomRepository {
    private final BaseRepository<ShoppingCart> baseRepository;

    @Override
    public BaseRepository<ShoppingCart> getRepository() {
        return baseRepository;
    }

    @Override
    public Page<ShoppingCart> findAllByAccountId(Integer accountId, Pageable pageable) {
        return baseRepository.fetchAllDataWithPagination(
            List.of(
                new WhereElements("account.id", accountId, WhereClauseType.EQUAL)
            ), pageable
        );
    }
}
