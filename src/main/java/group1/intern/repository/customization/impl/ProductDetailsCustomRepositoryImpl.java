package group1.intern.repository.customization.impl;

import group1.intern.model.Constant;
import group1.intern.model.Product;
import group1.intern.model.ProductDetail;
import group1.intern.repository.base.BaseRepository;
import group1.intern.repository.base.WhereClauseType;
import group1.intern.repository.base.WhereElements;
import group1.intern.repository.customization.ProductDetailsCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductDetailsCustomRepositoryImpl implements ProductDetailsCustomRepository {
    private final BaseRepository<ProductDetail> baseRepository;

    @PersistenceContext
    private EntityManager em;

    @Override
    public BaseRepository<ProductDetail> getRepository() {
        return baseRepository;
    }

    @Override
    public Page<ProductDetail> findByProductName(String name, Pageable pageable) {
        return baseRepository.fetchAllDataWithPagination(
            List.of(
                new WhereElements("product.name", "%" + name + "%", WhereClauseType.LIKE_IGNORE_CASE)
            ),
            pageable);
    }

    @Override
    public List<ProductDetail> findProductByFilter(List<Constant> constants) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProductDetail> cq = cb.createQuery(ProductDetail.class);
        Root<ProductDetail> pd = cq.from(ProductDetail.class);
        Join<ProductDetail, Product> p = pd.join("product"); // Assuming you have a proper mapping between ProductDetail and Product

        List<Predicate> predicates = new ArrayList<>();

        List<Integer> listStyleId = new ArrayList<>();
        List<Integer> listCategoryId = new ArrayList<>();
        List<Integer> listMaterialId = new ArrayList<>();
        List<Integer> listColorId = new ArrayList<>();

        for (Constant constant : constants) {
            switch (constant.getType()) {
                case "Style":
                    listStyleId.add(constant.getId());
                    break;
                case "Category":
                    listCategoryId.add(constant.getId());
                    break;
                case "Material":
                    listMaterialId.add(constant.getId());
                    break;
                case "Color":
                    listColorId.add(constant.getId());
                    break;
            }
        }

        if (!listStyleId.isEmpty()) {
            predicates.add(pd.get("style").get("id").in(listStyleId));
        }
        if (!listCategoryId.isEmpty()) {
            predicates.add(p.get("category").get("id").in(listCategoryId));
        }
        if (!listMaterialId.isEmpty()) {
            predicates.add(p.get("material").get("id").in(listMaterialId));
        }
        if (!listColorId.isEmpty()) {
            predicates.add(pd.get("color").get("id").in(listColorId));
        }

        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<ProductDetail> query = em.createQuery(cq);
        return query.getResultList();
    }
}
