package sun.intern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sun.intern.model.Embeddables.ProductDescription;
import sun.intern.model.Product;
import sun.intern.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SellingAndManagingShoesApplication {
    public static void main(String[] args) {
        SpringApplication.run(SellingAndManagingShoesApplication.class, args);
    }

}
