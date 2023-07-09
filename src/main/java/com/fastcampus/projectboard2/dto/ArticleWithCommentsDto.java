package com.fastcampus.projectboard2.dto;

import com.fastcampus.projectboard2.domain.Article;
import com.fastcampus.projectboard2.domain.ArticleComment;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleWithCommentsDto( // 게시글을 불러오면 그 댓글들도 모두 가져오기 위한 DTO
                                      // service코드에서 단건 조회때 사용
        Long id,
        UserAccountDto userAccountDto,
        Set<ArticleCommentDto> articleCommentDtos,
        String title,
        String content,
        Set<HashtagDto> hashtagDtos,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static ArticleWithCommentsDto of(Long id, UserAccountDto userAccountDto, Set<ArticleCommentDto> articleCommentDtos, String title, String content, Set<HashtagDto> hashtagDtos, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleWithCommentsDto(id, userAccountDto, articleCommentDtos, title, content, hashtagDtos, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleWithCommentsDto from(Article entity) {
        return new ArticleWithCommentsDto(
                entity.getId(), UserAccountDto.from(entity.getUserAccount()),
                entity.getArticleComments().stream().map(ArticleCommentDto::from)
                        .collect(Collectors.toCollection(LinkedHashSet::new)),
                entity.getTitle(), entity.getContent(),
                entity.getHashtags().stream().map(HashtagDto::from).collect(Collectors.toUnmodifiableSet()),
                entity.getCreatedAt(), entity.getCreatedBy(), entity.getModifiedAt(), entity.getModifiedBy()
        );
    }
}
