package group1.intern.service;

import group1.intern.bean.ProductDetailColors;
import group1.intern.bean.ProductDetailInfo;

import java.util.List;

public interface ProductService {
    ProductDetailInfo getProductDetailById(Integer id);
    List<ProductDetailColors> getProductDetailColors(Integer id);

}
