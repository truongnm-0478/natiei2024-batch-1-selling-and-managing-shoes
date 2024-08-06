package sun.intern.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sun.intern.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
