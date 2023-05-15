package com.fastcampus.projectboard2.domain;

import java.time.LocalDateTime;

public class ArticleComment {   // 댓글

    private Long id;
    private Article article;    // 게시글 (ID)
    private String content;     // 본문

    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;
}
