package group1.intern.repository.base;

import group1.intern.model.Product;
import group1.intern.model.ProductDetail;
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
public class ProductsBaseRepository implements BaseRepository<Product> {
    private final BaseRepository<ProductDetail> pdBaseRepository;

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Product> fetchAllDataWithoutPagination(List<WhereElements> whereElements, Sort sort,
            String... relationships) {
        List<Product> result = new ArrayList<>();
        StringBuilder query = new StringBuilder("""
                SELECT p
                    FROM Product p
                    LEFT JOIN FETCH p.category
                    LEFT JOIN FETCH p.material""");
        // if relationships is empty, then fetch all relationships
        relationships = CommonUtils.isEmptyOrNullList(relationships) ? new String[] { "productDetails" }
                : relationships;
        boolean isFirstQuery = false;
        for (var relationship : relationships) {
            switch (relationship) {
                case "productDetails":
                    if (!isFirstQuery) {
                        result = fetchAllDataWithFirstQuery(whereElements, query.toString(), sort, null);
                        isFirstQuery = true;
                    }
                    // fetch all relationships for each product detail
                    var tmp = pdBaseRepository.fetchAllDataWithoutPagination(
                            List.of(
                                    new WhereElements("product", result, WhereClauseType.IN)),
                            null);

                    // set product details for each product
                    result = result.stream().peek(
                            p -> p.setProductDetails(
                                    tmp.stream().filter(pq -> pq.getProduct().getId().equals(p.getId())).toList()))
                            .toList();
            }
        }
        return result;
    }

    @Override
    public Page<Product> fetchAllDataWithPagination(List<WhereElements> whereElements, Pageable pageable,
            String... relationships) {
        if (pageable == null)
            return null;
        List<Product> content = new ArrayList<>();
        StringBuilder query = new StringBuilder("""
                SELECT p
                    FROM Product p
                    LEFT JOIN FETCH p.category
                    LEFT JOIN FETCH p.material""");
        // if relationships is empty, then fetch all relationships
        relationships = CommonUtils.isEmptyOrNullList(relationships) ? new String[] { "productDetails" }
                : relationships;
        boolean isFirstQuery = false;
        for (var relationship : relationships) {
            switch (relationship) {
                case "productDetails":
                    if (!isFirstQuery) {
                        content = fetchAllDataWithFirstQuery(whereElements, query.toString(), null, pageable);
                        isFirstQuery = true;
                    }
                    // fetch all relationships for each product detail
                    var tmp = pdBaseRepository.fetchAllDataWithoutPagination(
                            List.of(
                                    new WhereElements("product", content, WhereClauseType.IN)),
                            null);

                    // set product details for each product
                    content = content.stream().peek(
                            p -> p.setProductDetails(
                                    tmp.stream().filter(pq -> pq.getProduct().getId().equals(p.getId())).toList()))
                            .toList();
            }
        }
        // count query
        String countResultHql = """
                SELECT COUNT(p) FROM Product p
                """ + CommonUtils.getWhereClause(whereElements, "p");
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
    public List<Product> fetchAllDataWithFirstQuery(List<WhereElements> whereElements, String baseQuery, Sort sort,
            Pageable pageable) {
        String whereClause = CommonUtils.getWhereClause(whereElements, "p");
        // sort clause
        String sortClause = (sort == null && pageable != null) ? CommonUtils.getSortClause(pageable.getSort(), "p")
                : CommonUtils.getSortClause(sort, "p");

        var query = em.createQuery(baseQuery + whereClause + sortClause, Product.class);

        // set parameters
        if (whereElements != null) {
            AtomicInteger index = new AtomicInteger(1);
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
