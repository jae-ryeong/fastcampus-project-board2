package com.fastcampus.projectboard2.service;

import com.fastcampus.projectboard2.domain.type.SearchType;
import com.fastcampus.projectboard2.dto.ArticleDto;
import com.fastcampus.projectboard2.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

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
}