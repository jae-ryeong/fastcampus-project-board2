package com.fastcampus.projectboard2.controller;

import com.fastcampus.projectboard2.domain.constant.FormStatus;
import com.fastcampus.projectboard2.domain.constant.SearchType;
import com.fastcampus.projectboard2.dto.UserAccountDto;
import com.fastcampus.projectboard2.dto.request.ArticleRequest;
import com.fastcampus.projectboard2.dto.response.ArticleResponse;
import com.fastcampus.projectboard2.dto.response.ArticleWithCommentsResponse;
import com.fastcampus.projectboard2.dto.security.BoardPrincipal;
import com.fastcampus.projectboard2.service.ArticleService;
import com.fastcampus.projectboard2.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

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
        map.addAttribute("searchTypes", SearchType.values());   // values()는 array[]로 넘겨준다.

        return "articles/index";
    }

    @GetMapping("/{articleId}")
    public String article(@PathVariable Long articleId, ModelMap map) {  // 게시글 세부내역 화면 전체에 뿌려주는 controller
        ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articleService.getArticleWithComments(articleId));   // 게시글과 댓글 모두 한번에 가져와준다.
        map.addAttribute("article", article);   // addAttribute로 html파일에 데이터를 넣어준다.
        map.addAttribute("articleComments", article.articleCommentsResponses());    // 댓글부분만 떼어서 따로 가져온다.

        map.addAttribute("totalCount", articleService.getArticleCount());   // 마지막 글을 판단하기 위해 총 글 개수가 필요해졌고 이를 위해 count 쿼리 사용, 서비스 로직 추가
        return "articles/detail";
    }

    @GetMapping("/search-hashtag")
    public String searchArticleHashtag(
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map
    ) {
        Page<ArticleResponse> articles = articleService.searchArticlesViaHashtag(searchValue, pageable).map(ArticleResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());
        List<String> hashtags = articleService.getHashtags();   // 모든 해시태그를 List로 만들고

        map.addAttribute("articles", articles);
        map.addAttribute("hashtags", hashtags);     // 여기서 search-hashtag.th.xml로 전달해준다.
        map.addAttribute("paginationBarNumbers", barNumbers);
        map.addAttribute("searchType", SearchType.HASHTAG);

        return "articles/search-hashtag";
    }

    @GetMapping("/form")
    public String articleForm(ModelMap map) {
        map.addAttribute("formStatus", FormStatus.CREATE);
        return "articles/form";
    }

    @PostMapping("/form")
    public String postNewArticle(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            ArticleRequest articleRequest) {
        articleService.saveArticle(articleRequest.toDto(boardPrincipal.toDto()));

        return "redirect:/articles";
    }

    @GetMapping("/{articleId}/form")
    public String updateArticleForm(@PathVariable Long articleId, ModelMap map) {
        ArticleResponse article = ArticleResponse.from(articleService.getArticle(articleId));

        map.addAttribute("article", article);
        map.addAttribute("formStatus", FormStatus.UPDATE);

        return "articles/form";
    }

    @PostMapping("/{articleId}/form")
    public String updateArticle(@PathVariable Long articleId,
                                @AuthenticationPrincipal BoardPrincipal boardPrincipal,
                                ArticleRequest articleRequest) {
        articleService.updateArticle(articleId, articleRequest.toDto(boardPrincipal.toDto()));
        return "redirect:/articles/" + articleId;
    }

    @PostMapping("/{articleId}/delete")
    public String deleteArticle(@PathVariable Long articleId,
                                @AuthenticationPrincipal BoardPrincipal boardPrincipal) {
        articleService.deleteArticle(articleId, boardPrincipal.getUsername());
        return "redirect:/articles";
    }




















}
