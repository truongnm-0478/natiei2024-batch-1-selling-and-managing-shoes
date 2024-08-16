package group1.intern.repository.customization.impl;

import group1.intern.model.Product;
import group1.intern.repository.base.BaseRepository;
import group1.intern.repository.customization.ProductsCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductsCustomRepositoryImpl implements ProductsCustomRepository {
    private final BaseRepository<Product> baseRepository;

    @Override
    public BaseRepository<Product> getRepository() {
        return baseRepository;
    }
}
