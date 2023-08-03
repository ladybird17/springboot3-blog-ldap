package me.yejin.springboot3blogldap.repository;


import java.util.Optional;
import me.yejin.springboot3blogldap.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-20
 */
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
}

