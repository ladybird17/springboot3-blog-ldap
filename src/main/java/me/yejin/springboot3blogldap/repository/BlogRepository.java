package me.yejin.springboot3blogldap.repository;

import me.yejin.springboot3blogldap.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-19
 */
public interface BlogRepository extends JpaRepository<Article, Long> {

}
