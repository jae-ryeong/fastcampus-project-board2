package com.fastcampus.projectboard2.dto.response;

import com.fastcampus.projectboard2.dto.ArticleCommentDto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ArticleCommentResponse(   // 댓글에 대한 정보만 담고있는 DTO
        Long id,
        String content,
        LocalDateTime createdAt,
        String email,
        String nickname
) {

    public static ArticleCommentResponse of(Long id, String content, LocalDateTime createdAt, String email, String nickname) {
        return new ArticleCommentResponse(id, content, createdAt, email, nickname);
    }

    public static ArticleCommentResponse from(ArticleCommentDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleCommentResponse(  // ArticleCommentDto로부터 id, content, createdAt, email, nickname 5가지만 뽑아온다.
                dto.id(),
                dto.content(),
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname
        );
    }
}
