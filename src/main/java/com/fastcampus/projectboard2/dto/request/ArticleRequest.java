package com.fastcampus.projectboard2.dto.request;

import com.fastcampus.projectboard2.dto.ArticleDto;
import com.fastcampus.projectboard2.dto.HashtagDto;
import com.fastcampus.projectboard2.dto.UserAccountDto;

import java.util.Set;

public record ArticleRequest(
        String title, String content
) {

    public static ArticleRequest of(String title, String content) {
        return new ArticleRequest(title, content);
    }

    public ArticleDto toDto(UserAccountDto userAccountDto) {
        return toDto(userAccountDto, null);
    }

    public ArticleDto toDto(UserAccountDto userAccountDto, Set<HashtagDto> hashtagDtos) {    // 유저계정정보를 받아 ArticleRequest와 합쳐 ArticleDto로 만든다.
        return ArticleDto.of(userAccountDto, title, content, hashtagDtos);
    }
}
