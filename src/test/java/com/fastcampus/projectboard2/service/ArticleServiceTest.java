package com.fastcampus.projectboard2.service;

import com.fastcampus.projectboard2.domain.Article;
import com.fastcampus.projectboard2.domain.UserAccount;
import com.fastcampus.projectboard2.domain.type.SearchType;
import com.fastcampus.projectboard2.dto.ArticleDto;
import com.fastcampus.projectboard2.dto.ArticleWithCommentsDto;
import com.fastcampus.projectboard2.dto.UserAccountDto;
import com.fastcampus.projectboard2.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스 로직 - 게시판")
@ExtendWith(MockitoExtension.class) // Mockito를 사용할 수 있게 해준다.
class ArticleServiceTest {

    @InjectMocks private ArticleService sut;
    @Mock private ArticleRepository articleRepository;

    @DisplayName("검색어 없이 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    public void NoSearchParameters_ArticlePage() throws Exception{
        //given
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findAll(pageable)).willReturn(Page.empty());

        //when
        Page<ArticleDto> articles = sut.searchArticles(null, null, pageable);

        //then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(pageable);
    }

    @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    public void SearchArticlesList() throws Exception{
        //given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(20);

        BDDMockito.given(articleRepository.findByTitle(searchKeyword, pageable)).willReturn(Page.empty());

        //when
        Page<ArticleDto> articles =  sut.searchArticles(searchType, searchKeyword, pageable);   // 제목, 본문, ID, 닉네임, 해시태그

        //then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByTitle(searchKeyword, pageable);
    }

    @DisplayName("게시글을 조회하면, 게시글 반환")
    @Test
    public void SearchArticleList() throws Exception{
        //given
        Long articleId = 1L;
        Article article = createArticle();

        //when
        ArticleWithCommentsDto dto = sut.getArticle(articleId);

        //then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());

        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("없는 게시글을 조회하면, 예외를 던진다.")
    @Test
    public void NoSearchArticle_ThrowException() throws Exception{
        //given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        //when
        Throwable t = catchThrowable(() -> sut.getArticle(articleId));

        //then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 없습니다 - articleId: " + articleId);
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글 정보를 입력하면, 게시글을 생성한다.")
    @Test
    public void CreateList() throws Exception{
        //given
        ArticleDto dto = createArticleDto();
        given(articleRepository.save(any(Article.class))).willReturn(createArticle());

        //when
        sut.saveArticle(dto);

        //then
        then(articleRepository).should().save(any(Article.class ));
    }

    @DisplayName("게시글의 수정 정보를 입력하면, 게시글을 수정한다.")
    @Test
    public void ModifiedList() throws Exception{
        //given
        Article article = createArticle();
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용", "#springboot");
        given(articleRepository.getReferenceById(dto.id())).willReturn(article);

        //when
        sut.updateArticle(dto);

        //then
        assertThat(article)
                .hasFieldOrPropertyWithValue("title", dto.title())
                .hasFieldOrPropertyWithValue("content", dto.content())
                .hasFieldOrPropertyWithValue("hashtag", dto.hashtag());
        then(articleRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("없는 게시글의 수정 정보를 입력하면, 경고 로그를 찍고 아무 것도 하지 않는다.")
    @Test
    public void NonArticleUpdate_LogPrint() throws Exception{
        //given
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용", "#springboot");

        given(articleRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        //when
        sut.updateArticle(dto);

        //then
        then(articleRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("게시글 ID를 입력하면, 게시글 삭제")
    @Test
    public void DeletedList() throws Exception{
        //given
        long articleId = 1L;
        willDoNothing().given(articleRepository).deleteById(articleId);

        //when
        sut.deleteArticle(articleId);

        //then
        then(articleRepository).should().deleteById(articleId); // save를 한번은 호출했는가 검사
    }

    private UserAccount createUserAccount() {
        return UserAccount.of(
                "wofud",
                "password",
                "wofud0321@naver.com",
                "wofud",
                "memo"
        );
    }

    private Article createArticle() {
        return Article.of(
                createUserAccount(),
                "title",
                "content",
                "#java"
        );
    }

    private ArticleDto createArticleDto(String title, String content, String hashtag) {
        return ArticleDto.of(1L, createUserAccountDto(), title, content,hashtag, LocalDateTime.now(), "wofud", LocalDateTime.now(), "wofud");
    }

    private ArticleDto createArticleDto() {
        return createArticleDto("title", "content", "#java");
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                1L, "wofud", "password", "wofud0321@naver.com", "wofud", "memo",
                LocalDateTime.now(), "wofud", LocalDateTime.now(), "wofud"
        );
    }
    
    
}