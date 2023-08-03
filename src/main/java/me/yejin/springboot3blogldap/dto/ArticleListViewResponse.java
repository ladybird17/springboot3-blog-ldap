package me.yejin.springboot3blogldap.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import me.yejin.springboot3blogldap.domain.Article;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-20
 */
@Getter
public class ArticleListViewResponse {

  private final Long id;
  private final String title;
  private final String content;

  public ArticleListViewResponse(Article article) {
    this.id = article.getId();
    this.title = article.getTitle();
    this.content = article.getContent();
  }
}
