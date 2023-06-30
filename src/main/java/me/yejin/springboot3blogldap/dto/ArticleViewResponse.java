package me.yejin.springboot3blogldap.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.yejin.springboot3blogldap.domain.Article;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-20
 */
@NoArgsConstructor
@Getter
public class ArticleViewResponse {

  private Long id;
  private String title;
  private String content;
  private LocalDateTime createdAt;

  public ArticleViewResponse(Article article) {
    this.id = article.getId();
    this.title = article.getTitle();
    this.content = article.getContent();
    this.createdAt = article.getCreatedAt();
  }
}