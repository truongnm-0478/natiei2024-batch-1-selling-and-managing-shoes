package group1.intern.repository.base;

import group1.intern.model.ProductQuantity;
import group1.intern.model.ShoppingCart;
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
public class ShoppingCartsBaseRepository implements BaseRepository<ShoppingCart> {
    private final BaseRepository<ProductQuantity> pqBaseRepository;
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ShoppingCart> fetchAllDataWithoutPagination(List<WhereElements> whereElements, Sort sort, String... relationships) {
        List<ShoppingCart> result = new ArrayList<>();
        StringBuilder query = new StringBuilder("""
            SELECT sc
                FROM ShoppingCart sc
                LEFT JOIN FETCH sc.account
                LEFT JOIN FETCH sc.productQuantity pq
                LEFT JOIN FETCH pq.size
                LEFT JOIN FETCH pq.productDetail pd
                LEFT JOIN FETCH pd.product p
                LEFT JOIN FETCH p.material
                LEFT JOIN FETCH p.category
                LEFT JOIN FETCH pd.color
                LEFT JOIN FETCH pd.style
                LEFT JOIN FETCH pd.images""");
        // if relationships is empty, then fetch all relationships
        relationships = CommonUtils.isEmptyOrNullList(relationships) ? new String[]{"default"} : relationships;
        boolean isFirstQuery = false;
        for (var relationship : relationships) {
            switch (relationship) {
                /* Now, don't have any relationships to fetch
                 *
                 * TODO: if have, add cases here
                 */
                default:
                    if (!isFirstQuery) {
                        result = fetchAllDataWithFirstQuery(whereElements, query.toString(), sort, null);
                        isFirstQuery = true;
                    }
            }
        }
        return result;
    }

    @Override
    public Page<ShoppingCart> fetchAllDataWithPagination(List<WhereElements> whereElements, Pageable pageable, String... relationships) {
        List<ShoppingCart> content = new ArrayList<>();
        StringBuilder query = new StringBuilder("""
            SELECT sc
                FROM ShoppingCart sc
                LEFT JOIN FETCH sc.account
                LEFT JOIN FETCH sc.productQuantity pq
                LEFT JOIN FETCH pq.size
                LEFT JOIN FETCH pq.productDetail pd
                LEFT JOIN FETCH pd.product p
                LEFT JOIN FETCH p.material
                LEFT JOIN FETCH p.category
                LEFT JOIN FETCH pd.color
                LEFT JOIN FETCH pd.style
                LEFT JOIN FETCH pd.images""");
        // if relationships is empty, then fetch all relationships
        relationships = CommonUtils.isEmptyOrNullList(relationships) ? new String[]{"default"} : relationships;
        boolean isFirstQuery = false;
        for (var relationship : relationships) {
            switch (relationship) {
                /* Now, don't have any relationships to fetch
                 *
                 * TODO: if have, add cases here
                 */
                default:
                    if (!isFirstQuery) {
                        content = fetchAllDataWithFirstQuery(whereElements, query.toString(), null, pageable);
                        isFirstQuery = true;
                    }
            }
        }
        // count query
        String countResultHql = """
            SELECT COUNT(sc) FROM ShoppingCart sc
            """ + CommonUtils.getWhereClause(whereElements, "sc");
        var countResultQuery = em.createQuery(countResultHql, Long.class);
        if (whereElements != null)
            for (int i = 0; i < whereElements.size(); i++)
                countResultQuery.setParameter(i + 1, whereElements.get(i).getValue());
        return new PageImpl<>(content, pageable, countResultQuery.getSingleResult());
    }

    @Override
    public List<ShoppingCart> fetchAllDataWithFirstQuery(List<WhereElements> whereElements, String baseQuery, Sort sort, Pageable pageable) {
        String whereClause = CommonUtils.getWhereClause(whereElements, "sc");
        // sort clause
        String sortClause = (sort == null && pageable != null) ? CommonUtils.getSortClause(pageable.getSort(), "sc") : CommonUtils.getSortClause(sort, "sc");

        var query = em.createQuery(baseQuery + whereClause + sortClause, ShoppingCart.class);

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
