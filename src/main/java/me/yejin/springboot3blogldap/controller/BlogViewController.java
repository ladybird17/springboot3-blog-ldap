package me.yejin.springboot3blogldap.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.yejin.springboot3blogldap.domain.Article;
import me.yejin.springboot3blogldap.dto.ArticleListViewResponse;
import me.yejin.springboot3blogldap.dto.ArticleViewResponse;
import me.yejin.springboot3blogldap.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-20
 */
@RequiredArgsConstructor
@Controller
public class BlogViewController {

  private final BlogService blogService;

  @GetMapping("/articles")
  public String getArticles(Model model){
    List<ArticleListViewResponse> articles = blogService.findAll().stream()
        .map(ArticleListViewResponse::new)
        .toList();
    model.addAttribute("articles", articles);

    return "articleList";
  }

  @GetMapping("/articles/{id}")
  public String getArticle(@PathVariable Long id, Model model){
    Article article = blogService.findById(id);
    model.addAttribute("article", new ArticleViewResponse(article));

    return "article";
  }

  @GetMapping("/new-article")
  public String newArticle(@RequestParam(required = false) Long id, Model model) {
    if (id == null) {
      model.addAttribute("article", new ArticleViewResponse());
    } else {
      Article article = blogService.findById(id);
      model.addAttribute("article", new ArticleViewResponse(article));
    }

    return "newArticle";
  }
}
