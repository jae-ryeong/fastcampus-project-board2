package com.fastcampus.projectboard2.repository.querydsl;

import com.fastcampus.projectboard2.domain.Article;
import com.fastcampus.projectboard2.domain.QArticle;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ArticleRepositoryCustomImpl extends QuerydslRepositorySupport implements ArticleRepositoryCustom{

    public ArticleRepositoryCustomImpl() {
        super(Article.class);
    }

    @Override
    public List<String> findAllDistinctHashtags() {
        QArticle article = QArticle.article;

        return  from(article) // 실무에서도 제네릭 타입(<>)을 생략하는 경우가 많은데 넣어주는게 좋다.
                .distinct()
                .select(article.hashtag)
                .where(article.hashtag.isNotNull())
                .fetch();
    }
}
