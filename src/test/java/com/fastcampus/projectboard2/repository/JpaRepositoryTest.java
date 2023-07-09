package com.fastcampus.projectboard2.repository;

import com.fastcampus.projectboard2.domain.Article;
import com.fastcampus.projectboard2.domain.Hashtag;
import com.fastcampus.projectboard2.domain.UserAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

//@ActiveProfiles("testdb")
@DisplayName("JPA 연결 테스트")
@Import(JpaRepositoryTest.TestConfig.class)    // 넣어주지 않으면 JpaConfig의 Auditing이 동작하지 않는다.
@DataJpaTest
class JpaRepositoryTest {

    private final ArticleRepository articleRepository;

    private final ArticleCommentRepository articleCommentRepository;

    private final UserAccountRepository userAccountRepository;
    private final HashtagRepository hashtagRepository;


    JpaRepositoryTest(@Autowired ArticleRepository articleRepository,
                      @Autowired ArticleCommentRepository articleCommentRepository,
                      @Autowired UserAccountRepository userAccountRepository,
                      @Autowired HashtagRepository hashtagRepository
    ) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.userAccountRepository = userAccountRepository;
        this.hashtagRepository = hashtagRepository;
    }


    @DisplayName("select 테스트")
    @Test
    public void test() throws Exception{
        //when
        List<Article> articles = articleRepository.findAll();

        //then
        assertThat(articles).isNotNull().hasSize(123); // classpath:resources/data.sql 참조
    }

    @DisplayName("insert 테스트")
    @Test
    public void givenTestDate_whenInserting_thenWorksFine() throws Exception{
        // Given
        long previousCount = articleRepository.count();

        UserAccount userAccount = userAccountRepository.save(UserAccount.of("newwofud", "pw", null, null, null));
        Article article = Article.of(userAccount, "new article", "new content");
        article.addHashtags(Set.of(Hashtag.of("spring")));

        //when
        articleRepository.save(article);

        //then
        assertThat(articleRepository.count()).isEqualTo(previousCount+1);
    }

    @DisplayName("update 테스트")
    @Test
    public void givenTestDate_whenUpdating_thenWorksFine() throws Exception{
        // Given
        Article article = articleRepository.findById(1L).orElseThrow();
        Hashtag updatedHashtag = Hashtag.of("springboot");
        article.clearHashtags();
        article.addHashtags(Set.of(updatedHashtag));

        //when
        Article savedArticle = articleRepository.saveAndFlush(article);

        //then
        assertThat(savedArticle.getHashtags())
                .hasSize(1)
                .extracting("hashtagName", String.class)
                .containsExactly(updatedHashtag.getHashtagName());
    }

    @DisplayName("delete 테스트")
    @Test
    public void givenTestDate_whenDeleting_thenWorksFine() throws Exception{
        // Given
        Article article = articleRepository.findById(1L).orElseThrow();
        long previousArticleCount = articleRepository.count();
        long previousArticleCommenrCount = articleCommentRepository.count();
        int deletedCommentsSize = article.getArticleComments().size();

        //when
        articleRepository.delete(article);

        //then
        assertThat(articleRepository.count()).isEqualTo(previousArticleCount-1);
        assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommenrCount - deletedCommentsSize);
    }

    @DisplayName("[Querydsl] 전체 hashtag 리스트에서 이름만 조회하기")
    @Test
    void givenNothing_whenQueryingHashtags_thenReturnsHashtagNames() {
        // Given

        // When
        List<String> hashtagNames = hashtagRepository.findAllHashtagNames();

        // Then
        assertThat(hashtagNames).hasSize(19);
    }

    @DisplayName("[Querydsl] hashtag로 페이징된 게시글 검색하기")
    @Test
    void givenHashtagNamesAndPageable_whenQueryingArticles_thenReturnsArticlePage() {
        // Given
        List<String> hashtagNames = List.of("blue", "crimson", "fuscia");
        Pageable pageable = PageRequest.of(0, 5, Sort.by(
                Sort.Order.desc("hashtags.hashtagName"),
                Sort.Order.asc("title")
        ));

        // When
        Page<Article> articlePage = articleRepository.findByHashtagNames(hashtagNames, pageable);

        // Then
        assertThat(articlePage.getContent()).hasSize(pageable.getPageSize());
        assertThat(articlePage.getContent().get(0).getTitle()).isEqualTo("Fusce posuere felis sed lacus.");
        assertThat(articlePage.getContent().get(0).getHashtags())
                .extracting("hashtagName", String.class)
                .containsExactly("fuscia");
        assertThat(articlePage.getTotalElements()).isEqualTo(17);
        assertThat(articlePage.getTotalPages()).isEqualTo(4);
    }

    @EnableJpaAuditing
    @TestConfiguration  // 테스트할때만 config하라는 애노테이션
    public static class TestConfig {
        @Bean
        public AuditorAware<String> auditorAware() {
            return () -> Optional.of("wofud");  // insert 테스트가 createdBy가 없어 에러가뜨므로
        }
    }
}