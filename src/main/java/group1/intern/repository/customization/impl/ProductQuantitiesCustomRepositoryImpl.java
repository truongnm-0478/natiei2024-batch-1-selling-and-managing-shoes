package group1.intern.repository.customization.impl;

import group1.intern.model.ProductQuantity;
import group1.intern.repository.base.BaseRepository;
import group1.intern.repository.customization.ProductQuantitiesCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductQuantitiesCustomRepositoryImpl implements ProductQuantitiesCustomRepository {
    private final BaseRepository<ProductQuantity> productQuantityBaseRepository;

    @Override
    public BaseRepository<ProductQuantity> getRepository() {
        return productQuantityBaseRepository;
    }
}
