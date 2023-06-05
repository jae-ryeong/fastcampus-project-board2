package com.fastcampus.projectboard2.controller;

import com.fastcampus.projectboard2.domain.type.SearchType;
import com.fastcampus.projectboard2.dto.response.ArticleResponse;
import com.fastcampus.projectboard2.dto.response.ArticleWithCommentsResponse;
import com.fastcampus.projectboard2.service.ArticleService;
import com.fastcampus.projectboard2.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/articles")
@Controller
public class ArticleController {

    private final ArticleService articleService;
    private final PaginationService paginationService;
    @GetMapping
    public String articles(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map) {

        Page<ArticleResponse> articles = articleService.searchArticles(searchType, searchValue, pageable).map(ArticleResponse::from);
        // searchArticles는 ArticleDto를 반환한다, 그 후 여기서 map으로 ArticleResponse로 바꿔준다.

        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());
        // pageable.getPageNumber()로 현재 페이지넘버를 알 수 있다.

        map.addAttribute("articles", articles);
        map.addAttribute("paginationBarNumbers", barNumbers);

        return "articles/index";
    }

    @GetMapping("/{articleId}")
    public String article(@PathVariable Long articleId, ModelMap map){  // 게시글 세부내역 화면 전체에 뿌려주는 controller
        ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articleService.getArticle(articleId));   // 게시글과 댓글 모두 한번에 가져와준다.
        map.addAttribute("article", article);   // addAttribute로 html파일에 데이터를 넣어준다.
        map.addAttribute("articleComments", article.articleCommentsResponses());    // 댓글부분만 떼어서 따로 가져온다.

        map.addAttribute("totalCount", articleService.getArticleCount());   // 마지막 글을 판단하기 위해 총 글 개수가 필요해졌고 이를 위해 count 쿼리 사용, 서비스 로직 추가
        return "articles/detail";
    }
}
