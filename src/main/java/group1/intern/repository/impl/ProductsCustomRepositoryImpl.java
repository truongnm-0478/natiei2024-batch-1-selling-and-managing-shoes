package group1.intern.repository.impl;

import group1.intern.model.Constant;
import group1.intern.model.Product;
import group1.intern.model.ProductDetail;
import group1.intern.repository.ProductsCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductsCustomRepositoryImpl implements ProductsCustomRepository {
    @PersistenceContext
    EntityManager em;

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
