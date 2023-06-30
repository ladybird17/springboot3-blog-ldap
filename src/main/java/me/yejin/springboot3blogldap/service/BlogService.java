package me.yejin.springboot3blogldap.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.yejin.springboot3blogldap.domain.Article;
import me.yejin.springboot3blogldap.dto.AddArticleRequest;
import me.yejin.springboot3blogldap.dto.UpdateArticleRequest;
import me.yejin.springboot3blogldap.repository.BlogRepository;
import org.springframework.stereotype.Service;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-19
 */
@RequiredArgsConstructor
@Service
public class BlogService {

  private final BlogRepository blogRepository;

  public Article save(AddArticleRequest request){
    return blogRepository.save(request.toEntity());
  }

  public List<Article> findAll(){
    return blogRepository.findAll();
  }

  public Article findById(long id){
    return blogRepository.findById(id)
        .orElseThrow(()->new IllegalArgumentException("not found: "+id));
  }

  public void delete(long id){
    blogRepository.deleteById(id);
  }

  @Transactional
  public Article update(long id, UpdateArticleRequest request){
    Article article = blogRepository.findById(id)
        .orElseThrow(()-> new IllegalArgumentException("not found: "+ id));

    article.update(request.getTitle(), request.getContent());

    return article;
  }


}
