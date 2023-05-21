package com.fastcampus.projectboard2.service;

import com.fastcampus.projectboard2.domain.Article;
import com.fastcampus.projectboard2.domain.type.SearchType;
import com.fastcampus.projectboard2.dto.ArticleDto;
import com.fastcampus.projectboard2.dto.ArticleUpdateDto;
import com.fastcampus.projectboard2.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("비즈니스 로직 - 게시판")
@ExtendWith(MockitoExtension.class) // Mockito를 사용할 수 있게 해준다.
class ArticleServiceTest {

    @InjectMocks private ArticleService sut;
    @Mock private ArticleRepository articleRepository;

    @DisplayName("게시글을 검색하면, 게시글 리스트를 반환")
    @Test
    public void SearchArticlesList() throws Exception{
        //given


        //when
        Page<ArticleDto> articles =  sut.searchArticles(SearchType.TITLE, "search keyword");   // 제목, 본문, ID, 닉네임, 해시태그

        //then
        assertThat(articles).isNotNull();
    }

    @DisplayName("게시글을 조회하면, 게시글 반환")
    @Test
    public void SearchArticleList() throws Exception{
        //given

        //when
        ArticleDto article =  sut.searchArticle(1L);

        //then
        assertThat(article).isNotNull();
    }

    @DisplayName("게시글을 정보를 입력하면, 게시글 생성")
    @Test
    public void CreateList() throws Exception{
        //given
        BDDMockito.given(articleRepository.save(ArgumentMatchers.any(Article.class))).willReturn(null);
        // 지금 이 테스트에서 없어도 된다.

        //when
        sut.saveArticle(ArticleDto.of(LocalDateTime.now(), "wofud", "title", "content", "#spring"));

        //then
        BDDMockito.then(articleRepository).should().save(ArgumentMatchers.any(Article.class)); // save를 한번은 호출했는가 검사
    }

    @DisplayName("수정 정보를 입력하면, 게시글 수정")
    @Test
    public void ModifiedList() throws Exception{
        //given
        BDDMockito.given(articleRepository.save(ArgumentMatchers.any(Article.class))).willReturn(null);
        // 지금 이 테스트에서 없어도 된다.

        //when
        sut.updateArticle(1L, ArticleUpdateDto.of("title", "content","#spring"));

        //then
        BDDMockito.then(articleRepository).should().save(ArgumentMatchers.any(Article.class)); // save를 한번은 호출했는가 검사
    }

    @DisplayName("게시글 ID를 입력하면, 게시글 삭제")
    @Test
    public void DeletedList() throws Exception{
        //given
        BDDMockito.willDoNothing().given(articleRepository).delete(ArgumentMatchers.any(Article.class));
        // 지금 이 테스트에서 없어도 된다.

        //when
        sut.deleteArticle(1L);

        //then
        BDDMockito.then(articleRepository).should().delete(ArgumentMatchers.any(Article.class)); // save를 한번은 호출했는가 검사
    }
}