package com.fastcampus.projectboard2.dto;

import com.fastcampus.projectboard2.domain.Article;
import com.fastcampus.projectboard2.domain.UserAccount;

import java.time.LocalDateTime;

public record ArticleDto(
        Long id,
        UserAccountDto userAccountDto,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy) {
    public static ArticleDto of(Long id, UserAccountDto userAccountDto, String title, String content, String hashtag, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleDto(
                id, userAccountDto, title, content, hashtag, createdAt, createdBy, modifiedAt, modifiedBy
        );
    }

    public static ArticleDto from(Article entity) { // Entity를 받아서 Dto로 변환해준다.
        return new ArticleDto(
                entity.getId(),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtag(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public Article toEntity() { // 반대로 Dto로부터 Entity를 생성하는 코드
        return Article.of(
                userAccountDto.toEntity(), title, content, hashtag
        );
    }
}
