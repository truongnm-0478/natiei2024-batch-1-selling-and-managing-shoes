package group1.intern.repository.customization.impl;

import group1.intern.model.ProductDetail;
import group1.intern.repository.base.BaseRepository;
import group1.intern.repository.base.WhereClauseType;
import group1.intern.repository.base.WhereElements;
import group1.intern.repository.customization.ProductDetailsCustomRepository;
import group1.intern.model.Enum.ProductGender;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
                        new WhereElements("product.name", "%" + name + "%", WhereClauseType.LIKE_IGNORE_CASE)),
                pageable);
    }

    @Override
    public Page<ProductDetail> findProductByFilter(
            List<Integer> listStyleId,
            List<Integer> listCategoryId,
            List<Integer> listMaterialId,
            List<Integer> listColorId,
            int genderFilter, 
            Pageable pageable) {


        List<WhereElements> whereElements = new ArrayList<>();
        if (!listStyleId.isEmpty())
            whereElements.add(new WhereElements("style.id", listStyleId, WhereClauseType.IN));
        if (!listCategoryId.isEmpty())
            whereElements.add(new WhereElements("product.category.id", listCategoryId, WhereClauseType.IN));
        if (!listMaterialId.isEmpty())
            whereElements.add(new WhereElements("product.material.id", listMaterialId, WhereClauseType.IN));
        if (!listColorId.isEmpty())
            whereElements.add(new WhereElements("color.id", listColorId, WhereClauseType.IN));
        switch (genderFilter) {
            case 2:
                whereElements.add(new WhereElements("gender", ProductGender.MALE, WhereClauseType.EQUAL));
                break;
            case 3:
                whereElements.add(new WhereElements("gender", ProductGender.FEMALE, WhereClauseType.EQUAL));
                break;
        }
        return baseRepository.fetchAllDataWithPagination(whereElements, pageable);
    }
}
