package sun.intern.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sun.intern.model.Constant;

@Repository
public interface ConstantRepository extends JpaRepository<Constant, Integer> {
}
