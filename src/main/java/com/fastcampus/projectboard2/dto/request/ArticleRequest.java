package com.fastcampus.projectboard2.dto.request;

import com.fastcampus.projectboard2.dto.ArticleDto;
import com.fastcampus.projectboard2.dto.UserAccountDto;

public record ArticleRequest(
        String title, String content, String hashtag
) {

    public static ArticleRequest of(String title, String content, String hashtag) {
        return new ArticleRequest(title, content, hashtag);
    }

    public ArticleDto toDto(UserAccountDto userAccountDto) {    // 유저계정정보를 받아 ArticleRequest와 합쳐 ArticleDto로 만든다.
        return ArticleDto.of(userAccountDto, title, content, hashtag);
    }
}
