package com.fastcampus.projectboard2.repository;

import com.fastcampus.projectboard2.domain.Article;
import com.fastcampus.projectboard2.domain.QArticle;
import com.fastcampus.projectboard2.repository.querydsl.ArticleRepositoryCustom;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ArticleRepository extends JpaRepository<Article, Long>,
        ArticleRepositoryCustom,
        QuerydslPredicateExecutor<Article>,
       QuerydslBinderCustomizer<QArticle>
{

    @Override
    default void customize(QuerydslBindings bindings, QArticle root) {
        bindings.excludeUnlistedProperties(true);
        /* 필터를 이용해 검색하고 싶을때
         이 줄은 bindings. included() 메서드 호출에 명시적으로 나열되지 않은 모든 속성을 제외하도록 바인딩을 구성합니다.
         지정된 속성(root.title, root.content, root.hashtag, root.createdAt, root.createdBy)만 바인딩에 포함되도록 한다. */
        bindings.including(root.title, root.content, root.hashtags, root.createdAt, root.createdBy); // 어떤필드를 필터로 사용할지 추가
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);  // title, 대소문자 구별 X
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.hashtags.any().hashtagName).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq); // API구현 29분
    }

    Page<Article> findByTitleContaining(String title, Pageable pageable);   // Containing란 모두 정확히 일치가 아닌, 포함하는 것으로도 검색 가능
    Page<Article> findByContentContaining(String content, Pageable pageable);
    Page<Article> findByUserAccount_UserIdContaining(String userId, Pageable pageable);
    Page<Article> findByUserAccount_NicknameContaining(String nickname, Pageable pageable);

    void deleteByIdAndUserAccount_UserId(Long articleId, String userId);

    // void deleteByIdAndUserAccount_UserId(Long articleId, String userId);
}
