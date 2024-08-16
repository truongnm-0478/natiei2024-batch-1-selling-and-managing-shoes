package group1.intern.repository.customization;

import group1.intern.model.Constant;
import group1.intern.model.ProductDetail;
import group1.intern.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductDetailsCustomRepository extends CustomRepository<ProductDetail, Integer, BaseRepository<ProductDetail>> {
    Page<ProductDetail> findByProductName(String name, Pageable pageable);

    List<ProductDetail> findProductByFilter(List<Constant> constants);
}
