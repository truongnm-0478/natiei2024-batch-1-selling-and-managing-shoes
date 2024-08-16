package group1.intern.repository.base;

import group1.intern.model.ProductDetail;
import group1.intern.model.ProductImage;
import group1.intern.model.ProductQuantity;
import group1.intern.util.util.CommonUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductDetailsBaseRepository implements BaseRepository<ProductDetail> {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ProductDetail> fetchAllDataWithoutPagination(List<WhereElements> whereElements, Sort sort, String... relationships) {
        List<ProductDetail> result = new ArrayList<>();
        StringBuilder query = new StringBuilder("""
            SELECT pd
                FROM ProductDetail pd
                LEFT JOIN FETCH pd.color
                LEFT JOIN FETCH pd.style
                LEFT JOIN FETCH pd.product p
                LEFT JOIN FETCH p.category
                LEFT JOIN FETCH p.material""");
        // if relationships is empty, then fetch all relationships
        relationships = CommonUtils.isEmptyOrNullList(relationships) ? new String[]{"quantities", "images"} : relationships;
        boolean isFirstQuery = false;
        for (var relationship : relationships) {
            switch (relationship) {
                case "quantities":
                    if (!isFirstQuery) {
                        query
                            .append(" LEFT JOIN FETCH pd.quantities pq LEFT JOIN FETCH pq.size")
                        ;
                        result = fetchAllDataWithFirstQuery(whereElements, query.toString(), sort, null);
                        isFirstQuery = true;
                    } else {
                        // fetch quantities for each product detail
                        var tmp = em.createQuery("""
                                    SELECT pq
                                        FROM ProductQuantity pq
                                        LEFT JOIN FETCH pq.size
                                    WHERE pq.productDetail IN :result""",
                                ProductQuantity.class)
                            .setParameter("result", result)
                            .getResultList();

                        // set quantities for each product detail
                        result = result.stream().peek(
                            pd -> pd.setQuantities(
                                tmp.stream().filter(pq -> pq.getProductDetail().getId().equals(pd.getId())).toList()
                            )
                        ).toList();
                    }
                    break;
                case "images":
                    if (!isFirstQuery) {
                        query
                            .append(" LEFT JOIN FETCH pd.images")
                        ;
                        result = fetchAllDataWithFirstQuery(whereElements, query.toString(), sort, null);
                        isFirstQuery = true;
                    } else {
                        // fetch images for each product detail
                        var tmp = em.createQuery("""
                                    SELECT i
                                        FROM ProductImage i
                                    WHERE i.productDetail IN :result""",
                                ProductImage.class)
                            .setParameter("result", result)
                            .getResultList();

                        // set images for each product detail
                        result = result.stream().peek(
                            pd -> pd.setImages(
                                tmp.stream().filter(i -> i.getProductDetail().getId().equals(pd.getId())).toList()
                            )
                        ).toList();
                    }
            }
        }
        return result;
    }

    @Override
    public Page<ProductDetail> fetchAllDataWithPagination(List<WhereElements> whereElements, Pageable pageable, String... relationships) {
        if (pageable == null) return null;
        List<ProductDetail> content = new ArrayList<>();
        StringBuilder query = new StringBuilder("""
            SELECT pd
                FROM ProductDetail pd
                LEFT JOIN FETCH pd.color
                LEFT JOIN FETCH pd.style
                LEFT JOIN FETCH pd.product p
                LEFT JOIN FETCH p.category
                LEFT JOIN FETCH p.material""");
        // if relationships is empty, then fetch all relationships
        relationships = CommonUtils.isEmptyOrNullList(relationships) ? new String[]{"quantities", "images"} : relationships;
        boolean isFirstQuery = false;
        for (var relationship : relationships) {
            switch (relationship) {
                case "quantities":
                    if (!isFirstQuery) {
                        query
                            .append(" LEFT JOIN FETCH pd.quantities pq LEFT JOIN FETCH pq.size");
                        content = fetchAllDataWithFirstQuery(whereElements, query.toString(), null, pageable);
                        isFirstQuery = true;
                    } else {
                        // fetch quantities for each product detail
                        var tmp = em.createQuery("""
                                    SELECT pq
                                        FROM ProductQuantity pq
                                        LEFT JOIN FETCH pq.size
                                    WHERE pq.productDetail IN :result""",
                                ProductQuantity.class)
                            .setParameter("result", content)
                            .getResultList();

                        // set quantities for each product detail
                        content = content.stream().peek(
                            pd -> pd.setQuantities(
                                tmp.stream().filter(pq -> pq.getProductDetail().getId().equals(pd.getId())).toList()
                            )
                        ).toList();
                    }
                    break;
                case "images":
                    if (!isFirstQuery) {
                        query
                            .append(" LEFT JOIN FETCH pd.images");
                        content = fetchAllDataWithFirstQuery(whereElements, query.toString(), null, pageable);
                        isFirstQuery = true;
                    } else {
                        // fetch images for each product detail
                        var tmp = em.createQuery("""
                                    SELECT i
                                        FROM ProductImage i
                                    WHERE i.productDetail IN :result""",
                                ProductImage.class)
                            .setParameter("result", content)
                            .getResultList();

                        // set images for each product detail
                        content = content.stream().peek(
                            pd -> pd.setImages(
                                tmp.stream().filter(i -> i.getProductDetail().getId().equals(pd.getId())).toList()
                            )
                        ).toList();
                    }
            }
        }
        // count query
        String countResultHql = """
            SELECT COUNT(pd) FROM ProductDetail pd
            """ + CommonUtils.getWhereClause(whereElements, "pd");
        var countResultQuery = em.createQuery(countResultHql, Long.class);
        if (whereElements != null)
            for (int i = 0; i < whereElements.size(); i++)
                countResultQuery.setParameter(i + 1, whereElements.get(i).getValue());
        return new PageImpl<>(content, pageable, countResultQuery.getSingleResult());
    }

    @Override
    public List<ProductDetail> fetchAllDataWithFirstQuery(List<WhereElements> whereElements, String baseQuery, Sort sort, Pageable pageable) {
        String whereClause = CommonUtils.getWhereClause(whereElements, "pd");
        // sort clause
        String sortClause = (sort == null && pageable != null) ? CommonUtils.getSortClause(pageable.getSort(), "pd") : CommonUtils.getSortClause(sort, "pd");

        var query = em.createQuery(baseQuery + whereClause + sortClause, ProductDetail.class);

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
