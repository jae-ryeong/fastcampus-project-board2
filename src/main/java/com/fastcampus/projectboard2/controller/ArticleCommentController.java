package com.fastcampus.projectboard2.controller;

import com.fastcampus.projectboard2.dto.UserAccountDto;
import com.fastcampus.projectboard2.dto.request.ArticleCommentRequest;
import com.fastcampus.projectboard2.service.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/comments")
@Controller
public class ArticleCommentController {

    private final ArticleCommentService articleCommentService;

    @PostMapping("/new")
    public String postNewArticleComment(ArticleCommentRequest articleCommentRequest){
        // TODO: 인증 정보를 넣어줘야 한다.
        articleCommentService.saveArticleComment(articleCommentRequest.toDto(UserAccountDto.of(
                "wofud", "pw", "wofud0321@naver.com", null, null)));  // userId가 uno, uno2가 아니면 에러가난다.
        // articleCommentRequest.toDto는 articleCommentDto를 반환한다.
        return "redirect:/articles/" + articleCommentRequest.articleId();
    }

    @PostMapping("/{commentId}/delete")
    public String deleteArticleComment(@PathVariable Long commentId,Long articleId) {
        // 지우고 뷰로 돌아갈때 문제가 되므로 현재 articleId도 알아야한다.
        articleCommentService.deleteArticleComment(commentId);
        return "redirect:/articles/" + articleId;
    }
}
