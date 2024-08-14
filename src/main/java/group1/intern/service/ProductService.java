package group1.intern.service;

import group1.intern.bean.ProductDetailColors;
import group1.intern.bean.ProductDetailInfo;
import group1.intern.model.ProductDetail;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    ProductDetailInfo getProductDetailById(Integer id);
    List<ProductDetailColors> getProductDetailColors(Integer id);
    Page<ProductDetail> getProductsByName(String name, int page, int size);
}
