package group1.intern.repository.base;

import group1.intern.model.Order;
import group1.intern.model.ProductQuantity;
import group1.intern.util.util.CommonUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class OrdersBaseRepository implements BaseRepository<Order> {
    private final BaseRepository<ProductQuantity> pqBaseRepository;
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Order> fetchAllDataWithoutPagination(List<WhereElements> whereElements, Sort sort,
            String... relationships) {
        List<Order> result = new ArrayList<>();
        StringBuilder query = new StringBuilder("""
                SELECT o
                    FROM Order o
                    LEFT JOIN FETCH o.account
                """);
        // if relationships is empty, then fetch all relationships
        relationships = CommonUtils.isEmptyOrNullList(relationships) ? new String[] { "orderDetails" } : relationships;
        boolean isFirstQuery = false;
        for (var relationship : relationships) {
            switch (relationship) {
                case "orderDetails":
                    if (!isFirstQuery) {
                        query
                                .append(" LEFT JOIN FETCH o.orderDetails od LEFT JOIN FETCH od.productQuantity opq LEFT JOIN FETCH opq.size");
                        result = fetchAllDataWithFirstQuery(whereElements, query.toString(), sort, null);
                        isFirstQuery = true;
                    }
                    // fetch product quantities
                    var pqIds = new ArrayList<Integer>();
                    for (var order : result)
                        for (var od : order.getOrderDetails())
                            pqIds.add(od.getProductQuantity().getId());
                    var tmp = pqBaseRepository.fetchAllDataWithoutPagination(
                            List.of(
                                    new WhereElements("id", pqIds, WhereClauseType.IN)),
                            null);

                    // set product quantities to order details
                    for (var order : result)
                        for (var od : order.getOrderDetails())
                            od.setProductQuantity(
                                    tmp.stream().filter(pq -> pq.getId().equals(od.getProductQuantity().getId()))
                                            .findFirst().orElse(null));
            }
        }
        return result;
    }

    @Override
    public Page<Order> fetchAllDataWithPagination(List<WhereElements> whereElements, Pageable pageable,
            String... relationships) {
        List<Order> content = new ArrayList<>();
        StringBuilder query = new StringBuilder("""
                SELECT o
                    FROM Order o
                    LEFT JOIN FETCH o.account
                """);
        // if relationships is empty, then fetch all relationships
        relationships = CommonUtils.isEmptyOrNullList(relationships) ? new String[] { "orderDetails" } : relationships;
        boolean isFirstQuery = false;
        for (var relationship : relationships) {
            switch (relationship) {
                case "orderDetails":
                    if (!isFirstQuery) {
                        query
                                .append(" LEFT JOIN FETCH o.orderDetails od LEFT JOIN FETCH od.productQuantity opq LEFT JOIN FETCH opq.size");
                        content = fetchAllDataWithFirstQuery(whereElements, query.toString(), null, pageable);
                        isFirstQuery = true;
                    }
                    // fetch product quantities
                    var pqIds = new ArrayList<Integer>();
                    for (var order : content)
                        for (var od : order.getOrderDetails())
                            pqIds.add(od.getProductQuantity().getId());
                    var tmp = pqBaseRepository.fetchAllDataWithoutPagination(
                            List.of(
                                    new WhereElements("id", pqIds, WhereClauseType.IN)),
                            null);

                    // set product quantities to order details
                    for (var order : content)
                        for (var od : order.getOrderDetails())
                            od.setProductQuantity(
                                    tmp.stream().filter(pq -> pq.getId().equals(od.getProductQuantity().getId()))
                                            .findFirst().orElse(null));
            }
        }
        // count query
        String countResultHql = """
                SELECT COUNT(o) FROM Order o
                """ + CommonUtils.getWhereClause(whereElements, "o");
        var countResultQuery = em.createQuery(countResultHql, Long.class);
        if (whereElements != null) {
            AtomicInteger index = new AtomicInteger(1);
            for (var element : whereElements) {
                if (!element.getType().isNoNeedParamType()) {
                    countResultQuery.setParameter(index.getAndIncrement(), element.getValue());
                }
            }
        }
        return new PageImpl<>(content, pageable, countResultQuery.getSingleResult());
    }

    @Override
    public List<Order> fetchAllDataWithFirstQuery(List<WhereElements> whereElements, String baseQuery, Sort sort,
            Pageable pageable) {
        String whereClause = CommonUtils.getWhereClause(whereElements, "o");
        // sort clause
        String sortClause = (sort == null && pageable != null) ? CommonUtils.getSortClause(pageable.getSort(), "o")
                : CommonUtils.getSortClause(sort, "o");

        var query = em.createQuery(baseQuery + whereClause + sortClause, Order.class);

        // set parameters
        if (whereElements != null) {
            AtomicInteger index = new AtomicInteger(0);
            for (var element : whereElements) {
                if (!element.getType().isNoNeedParamType()) {
                    query.setParameter(index.getAndIncrement(), element.getValue());
                }
            }
        }

        // set pageable
        if (pageable != null)
            query
                    .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                    .setMaxResults(pageable.getPageSize());

        return query.getResultList();
    }
}
