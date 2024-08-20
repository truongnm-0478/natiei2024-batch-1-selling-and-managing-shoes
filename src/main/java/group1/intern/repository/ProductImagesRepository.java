package group1.intern.repository;

import group1.intern.model.Product;
import group1.intern.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImagesRepository extends JpaRepository<ProductImage, Integer> {
}