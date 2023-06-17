package com.fastcampus.projectboard2.dto.request;

import com.fastcampus.projectboard2.dto.ArticleCommentDto;
import com.fastcampus.projectboard2.dto.UserAccountDto;

public record ArticleCommentRequest(Long articleId, String content) {

    public static ArticleCommentRequest of(Long articleId, String content) {
        return new ArticleCommentRequest(articleId, content);
    }

    public ArticleCommentDto toDto(UserAccountDto userAccountDto) { // UserAccountDto를 통해서 Dto를 만든다.
        // 댓글 dto를 만들때 user의 정보는 request로부터 받지 못하므로 외부에서 입력해준다는거 같다.
        return ArticleCommentDto.of(articleId, userAccountDto, content);
    }
}
