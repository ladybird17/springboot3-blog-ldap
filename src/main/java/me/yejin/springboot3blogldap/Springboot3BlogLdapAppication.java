package me.yejin.springboot3blogldap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-13
 */
@EnableJpaAuditing //created_at, updated_at 자동 업데이트
@SpringBootApplication
public class Springboot3BlogLdapAppication {
  public static void main(String[] args) {
    SpringApplication.run(Springboot3BlogLdapAppication.class, args);
  }
}
