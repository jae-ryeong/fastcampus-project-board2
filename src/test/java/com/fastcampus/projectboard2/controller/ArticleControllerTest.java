package com.fastcampus.projectboard2.controller;

import com.fastcampus.projectboard2.config.SecurityConfig;
import com.fastcampus.projectboard2.dto.ArticleWithCommentsDto;
import com.fastcampus.projectboard2.dto.UserAccountDto;
import com.fastcampus.projectboard2.service.ArticleService;
import com.fastcampus.projectboard2.service.PaginationService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러- 게시글")
@Import(SecurityConfig.class)
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    private final MockMvc mvc;

    @MockBean private ArticleService articleService; // 필드주입
    @MockBean private PaginationService paginationService;

    public ArticleControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }   // 생성자 주입


    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 정상 호출")
    @Test
    public void ArticleView() throws Exception{
        //given
        given(articleService.searchArticles(eq(null), eq(null), any(Pageable.class))).willReturn(Page.empty());  // any는 Matcher이며, 필드 중 일부만 Matcher를 할 수 없으므로, null 값들도 eq로 matcher해준다.
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));   // anyInt로 아무값이나 테스트가 넣어준다. any는 널도 허용이라 여기서는 X
        //when & then
        mvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))  // view이므로 text_html
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("paginationBarNumbers"));

        then(articleService).should().searchArticles(eq(null), eq(null), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }


    @DisplayName("[view][GET] 게시글 상세 페이지 - 정상 호출")
    @Test
    public void ArticleView_detail() throws Exception{
        //given
        Long ArticleId = 1L;
        given(articleService.getArticle(ArticleId)).willReturn(createArticleWithCommentsDto());

        //when & then
        mvc.perform(get("/articles/" + ArticleId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("articleComments"));

        then(articleService).should().getArticle(ArticleId);
    }

    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 페이징, 정렬 기능")
    @Test
    void givenPagingAndSortingParams_whenSearchingArticlesPage_thenReturnsArticlesPage() throws Exception{
        //given
        String sortName = "title";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 5;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));   // title을 기준으로 내림차준 정렬
        List<Integer> barNumbers = List.of(1, 2, 3, 4, 5);

        given(articleService.searchArticles(null, null, pageable)).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages())).willReturn(barNumbers);

        //when & then
        mvc.perform(
                get("/articles")
                        .queryParam("page", String.valueOf(pageNumber))
                        .queryParam("size", String.valueOf(pageSize))
                        .queryParam("sort", sortName + "," + direction))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attribute("paginationBarNumbers", barNumbers));

        then(articleService).should().searchArticles(null, null, pageable);
        then(paginationService).should().getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages());
    }

    @Disabled
    @DisplayName("[view][GET] 게시글 검색 전용 페이지 - 정상 호출")
    @Test
    public void Article_Search_View() throws Exception{
        //when & then
        mvc.perform(get("/articles/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))  // view이므로 text_html
                .andExpect(model().attributeExists("articles/search"));
    }

    @Disabled
    @DisplayName("[view][GET] 게시글 해시태그 검색 페이지 - 정상 호출")
    @Test
    public void Article_Hashtag_Search() throws Exception{
        //when & then
        mvc.perform(get("/articles/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("articles/search-hashtag"));
    }

    private ArticleWithCommentsDto createArticleWithCommentsDto() {
        return ArticleWithCommentsDto.of(
                1L,
                createUserAccountDto(),
                Set.of(),   // 테스트 코드라서 빈 껍데기를 전달해준듯 싶다. (내 예상)
                "title",
                "content",
                "#java",
                LocalDateTime.now(),
                "wofud",
                LocalDateTime.now(),
                "wofud"
        );
    }
    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                1L, "wofud", "password", "wofud0321@naver.com", "wofud", "memo",
                LocalDateTime.now(), "wofud", LocalDateTime.now(), "wofud"
        );
    }
}
