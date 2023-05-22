package com.fastcampus.projectboard2.repository;

import com.fastcampus.projectboard2.domain.Article;
import com.fastcampus.projectboard2.domain.QArticle;
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
        QuerydslPredicateExecutor<Article>,
       QuerydslBinderCustomizer<QArticle>
{
    @Override
    default void customize(QuerydslBindings bindings, QArticle root) {
        bindings.excludeUnlistedProperties(true);   // 필터를 이용해 검색하고 싶을때
        bindings.including(root.title, root.content, root.hashtag, root.createdAt, root.createdBy); // 어떤필드를 필터로 사용할지 추가
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);  // title, 대소문자 구별 X
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.hashtag).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq); // API구현 29분
    }

    Page<Article> findByTitle(String title, Pageable pageable);
}
