package com.fastcampus.projectboard2.dto;

import com.fastcampus.projectboard2.domain.Article;
import com.fastcampus.projectboard2.domain.Hashtag;
import com.fastcampus.projectboard2.domain.UserAccount;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleDto(   // record에 대해 좀 더 공부할것
        Long id,
        UserAccountDto userAccountDto,
        String title,
        String content,
        Set<HashtagDto> hashtagDtos,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy) {

    public static ArticleDto of(UserAccountDto userAccountDto, String title, String content, Set<HashtagDto> hashtagDtos) {
        return new ArticleDto(null, userAccountDto, title, content, hashtagDtos, null, null, null, null);
    }
    public static ArticleDto of(Long id, UserAccountDto userAccountDto, String title, String content, Set<HashtagDto> hashtagDtos, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleDto(
                id, userAccountDto, title, content, hashtagDtos, createdAt, createdBy, modifiedAt, modifiedBy
        );
    }

    public static ArticleDto from(Article entity) { // Entity를 받아서 Dto로 변환해준다.
        return new ArticleDto(
                entity.getId(),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtags().stream().map(HashtagDto::from).collect(Collectors.toUnmodifiableSet()),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

/*    public Article toEntity() { // 반대로 Dto로부터 Entity를 생성하는 코드
        return Article.of(
                userAccountDto.toEntity(), title, content, hashtag
        );
    }*/

    public Article toEntity(UserAccount userAccount) {
        return Article.of(userAccount, title, content);
    }
}
