package group1.intern.repository.customization.impl;

import group1.intern.model.Enum.OrderStatus;
import group1.intern.model.Order;
import group1.intern.repository.base.BaseRepository;
import group1.intern.repository.base.WhereClauseType;
import group1.intern.repository.base.WhereElements;
import group1.intern.repository.customization.OrdersCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrdersCustomRepositoryImpl implements OrdersCustomRepository {
    private final BaseRepository<Order> baseRepository;

    @Override
    public BaseRepository<Order> getRepository() {
        return baseRepository;
    }

    @Override
    public Page<Order> findAllByAccount_Id(Integer accountId, Pageable pageable) {
        return baseRepository.fetchAllDataWithPagination(
            List.of(
                new WhereElements("account.id", accountId, WhereClauseType.EQUAL)
            ),
            pageable
        );
    }

    @Override
    public Page<Order> findAllByAccount_IdAndStatus(Integer accountId, OrderStatus status, Pageable pageable) {
        return baseRepository.fetchAllDataWithPagination(
            List.of(
                new WhereElements("account.id", accountId, WhereClauseType.EQUAL),
                new WhereElements("status", status, WhereClauseType.EQUAL)
            ),
            pageable
        );
    }
}
