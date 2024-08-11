package group1.intern.repository;

import group1.intern.model.Constant;
import group1.intern.model.ProductDetail;

import java.util.List;

public interface ProductsCustomRepository {
    List<ProductDetail> findProductByFilter(List<Constant> constants);
}
