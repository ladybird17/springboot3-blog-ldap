package me.yejin.springboot3blogldap.dto;

import lombok.Getter;
import me.yejin.springboot3blogldap.domain.Article;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-20
 */
@Getter
public class ArticleResponse {

  private final String title;
  private final String content;

  public ArticleResponse(Article article) {
    this.title = article.getTitle();
    this.content = article.getContent();
  }
}
