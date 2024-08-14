package group1.intern.repository;

import group1.intern.model.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductDetailCustomRepository {
    Page<ProductDetail> findByProductName(String name, Pageable pageable);
}
