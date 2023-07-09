package com.fastcampus.projectboard2.dto;

import com.fastcampus.projectboard2.domain.Hashtag;

import java.time.LocalDateTime;
import java.util.Set;

public record HashtagWithArticlesDto(
        Long id,
        Set<ArticleDto> articles,
        String hashtagName,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static HashtagWithArticlesDto of(Set<ArticleDto> articles, String hashtagName) {
        return new HashtagWithArticlesDto(null,articles,hashtagName,null,null,null,null);
    }

    public static HashtagWithArticlesDto of(Long id, Set<ArticleDto> articles, String hashtagName, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new HashtagWithArticlesDto(id, articles, hashtagName, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public Hashtag toEntity() {
        return Hashtag.of(hashtagName);
    }
}
