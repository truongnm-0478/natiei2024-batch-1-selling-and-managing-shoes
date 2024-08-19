package group1.intern.repository;

import group1.intern.model.Constant;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConstantRepository extends JpaRepository<Constant, Integer> {
    List<Constant> findByType(String type);
}
