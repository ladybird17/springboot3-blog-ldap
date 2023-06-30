package me.yejin.springboot3blogldap.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.yejin.springboot3blogldap.domain.Article;
import me.yejin.springboot3blogldap.dto.AddArticleRequest;
import me.yejin.springboot3blogldap.dto.ArticleResponse;
import me.yejin.springboot3blogldap.dto.UpdateArticleRequest;
import me.yejin.springboot3blogldap.service.BlogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-19
 */
@RequiredArgsConstructor
@RestController
public class BlogApiController {

  private final BlogService blogService;

  @PostMapping("/api/articles")
  public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request){
    Article savedArticle = blogService.save(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
  }

  @GetMapping("/api/articles")
  public ResponseEntity<List<ArticleResponse>> findAllArticles(){
    List<ArticleResponse> articles = blogService.findAll()
        .stream()
        .map(ArticleResponse::new)
        .toList();
    return ResponseEntity.ok().body(articles);
  }

  @GetMapping("/api/articles/{id}")
  public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id){
    Article article = blogService.findById(id);
    return ResponseEntity.ok().body(new ArticleResponse(article));
  }

  @DeleteMapping("/api/articles/{id}")
  public ResponseEntity<Void> deleteArticles(@PathVariable long id){
    blogService.delete(id);

    return ResponseEntity.ok().build();
  }

  @PutMapping("/api/articles/{id}")
  public ResponseEntity<ArticleResponse> updatedArticles(@PathVariable long id,
      @RequestBody UpdateArticleRequest request){
    Article updatedArticles = blogService.update(id, request);

    return ResponseEntity.ok().body(new ArticleResponse(updatedArticles));
  }


}
