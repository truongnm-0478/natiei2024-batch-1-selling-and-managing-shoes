package group1.intern.repository.impl;

import group1.intern.model.Product;
import group1.intern.model.ProductDetail;
import group1.intern.repository.ProductDetailCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductDetailCustomRepositoryImpl implements ProductDetailCustomRepository {
    @PersistenceContext
    EntityManager em;

    @Override
    public Page<ProductDetail> findByProductName(String name, Pageable pageable) {
        // Create query
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProductDetail> cq = cb.createQuery(ProductDetail.class);
        Root<ProductDetail> pd = cq.from(ProductDetail.class);
        Join<ProductDetail, Product> p = pd.join("product");
        Predicate namePredicate = cb.like(cb.lower(p.get("name")), "%" + name.toLowerCase() + "%");

        cq.where(namePredicate);

        TypedQuery<ProductDetail> query = em.createQuery(cq);

        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int firstResult = pageNumber * pageSize;

        query.setFirstResult(firstResult);
        query.setMaxResults(pageSize);

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ProductDetail> countRoot = countQuery.from(ProductDetail.class);
        Join<ProductDetail, Product> countJoin = countRoot.join("product");
        countQuery.select(cb.count(countRoot)).where(cb.like(countJoin.get("name"), "%" + name + "%"));
        TypedQuery<Long> countQueryTyped = em.createQuery(countQuery);
        long total = countQueryTyped.getSingleResult();

        List<ProductDetail> productDetails = query.getResultList();
//
        return new PageImpl<>(productDetails, pageable, total);
    }
}
