package com.fastcampus.projectboard2.dto.response;

import com.fastcampus.projectboard2.dto.ArticleDto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ArticleResponse(  // 순수 Article 정보만 있는듯 하다 (UserAccount X), 사용자가 봐야하는(알면 안되는) 정보만 포함한듯 싶다.
                                // ArticleDto로 사용자로부터 정보를 받고, ArticleResponse를 사용자에게 공개한다.
        Long id,    // 어떤 엔티티의 id인지 아직 잘 모르겠고, detail.th파일에서도 받고있지 않다.
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String email,
        String nickname
) implements Serializable
{
    public static ArticleResponse of(Long id, String title, String content, String hashtag, LocalDateTime createdAt, String email, String nickname) {
        return new ArticleResponse(id, title, content, hashtag, createdAt, email, nickname);
    }

    public static ArticleResponse from(ArticleDto dto) {
        String nickname = dto.userAccountDto().nickname();  // nickname은 userAccount에서 가져온다.
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();   // nickname이 없으면 userId를 nickname으로 사용
        }

        return new ArticleResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtag(),
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname
        );
    }
}
