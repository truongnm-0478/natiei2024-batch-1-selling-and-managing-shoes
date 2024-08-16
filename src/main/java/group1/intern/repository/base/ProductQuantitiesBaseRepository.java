package group1.intern.repository.base;

import group1.intern.model.ProductDetail;
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

@Component
@RequiredArgsConstructor
public class ProductQuantitiesBaseRepository implements BaseRepository<ProductQuantity> {
    private final BaseRepository<ProductDetail> pdBaseRepository;
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ProductQuantity> fetchAllDataWithoutPagination(List<WhereElements> whereElements, Sort sort, String... relationships) {
        List<ProductQuantity> result = new ArrayList<>();
        StringBuilder query = new StringBuilder("""
            SELECT pq
                FROM ProductQuantity pq
                LEFT JOIN FETCH pq.size
            """);
        // if relationships is empty, then fetch all relationships
        relationships = CommonUtils.isEmptyOrNullList(relationships) ? new String[]{"productDetail"} : relationships;
        boolean isFirstQuery = false;
        for (var relationship : relationships) {
            switch (relationship) {
                case "productDetail":
                    if (!isFirstQuery) {
                        result = fetchAllDataWithFirstQuery(whereElements, query.toString(), sort, null);
                        isFirstQuery = true;
                    }
                    // fetch product details for each product quantity
                    var tmp = pdBaseRepository.fetchAllDataWithoutPagination(
                        List.of(
                            new WhereElements("id", result.stream().map(pq -> pq.getProductDetail().getId()).toList(), WhereClauseType.IN)
                        ),
                        null,
                        "images"
                    );

                    // set product details for each product quantity
                    result = result.stream().peek(
                        pq -> pq.setProductDetail(
                            tmp.stream().filter(pd -> pd.getId().equals(pq.getProductDetail().getId())).findFirst().orElse(null)
                        )
                    ).toList();
            }
        }
        return result;
    }

    @Override
    public Page<ProductQuantity> fetchAllDataWithPagination(List<WhereElements> whereElements, Pageable pageable, String... relationships) {
        if (pageable == null) return null;
        List<ProductQuantity> content = new ArrayList<>();
        StringBuilder query = new StringBuilder("""
            SELECT pq
                FROM ProductQuantity pq
                LEFT JOIN FETCH pq.size
            """);
        // if relationships is empty, then fetch all relationships
        relationships = CommonUtils.isEmptyOrNullList(relationships) ? new String[]{"productDetail"} : relationships;
        boolean isFirstQuery = false;
        for (var relationship : relationships) {
            switch (relationship) {
                case "productDetail":
                    if (!isFirstQuery) {
                        content = fetchAllDataWithFirstQuery(whereElements, query.toString(), null, pageable);
                        isFirstQuery = true;
                    }
                    // fetch product details for each product quantity
                    var tmp = pdBaseRepository.fetchAllDataWithoutPagination(
                        List.of(
                            new WhereElements("id", content.stream().map(pq -> pq.getProductDetail().getId()).toList(), WhereClauseType.IN)
                        ),
                        null,
                        "images"
                    );

                    // set product details for each product quantity
                    content = content.stream().peek(
                        pq -> pq.setProductDetail(
                            tmp.stream().filter(pd -> pd.getId().equals(pq.getProductDetail().getId())).findFirst().orElse(null)
                        )
                    ).toList();
            }
        }
        // count query
        String countResultHql = """
            SELECT COUNT(pq) FROM ProductQuantity pq
            """ + CommonUtils.getWhereClause(whereElements, "pq");
        var countResultQuery = em.createQuery(countResultHql, Long.class);
        if (whereElements != null)
            for (int i = 0; i < whereElements.size(); i++)
                countResultQuery.setParameter(i + 1, whereElements.get(i).getValue());
        return new PageImpl<>(content, pageable, countResultQuery.getSingleResult());
    }

    @Override
    public List<ProductQuantity> fetchAllDataWithFirstQuery(List<WhereElements> whereElements, String baseQuery, Sort sort, Pageable pageable) {
        String whereClause = CommonUtils.getWhereClause(whereElements, "pq");
        // sort clause
        String sortClause = (sort == null && pageable != null) ? CommonUtils.getSortClause(pageable.getSort(), "pq") : CommonUtils.getSortClause(sort, "pq");

        var query = em.createQuery(baseQuery + whereClause + sortClause, ProductQuantity.class);

        // set parameters
        if (whereElements != null)
            for (int i = 0; i < whereElements.size(); i++)
                query.setParameter(i + 1, whereElements.get(i).getValue());

        // set pageable
        if (pageable != null)
            query
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                .setMaxResults(pageable.getPageSize());

        return query.getResultList();
    }
}
