package group1.intern.repository;

import group1.intern.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    List<RefreshToken> findByAccount_Id(int accountId);
}
