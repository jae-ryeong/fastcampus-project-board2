package com.fastcampus.projectboard2.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@EntityListeners(AuditingEntityListener.class)
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleComment {   // 댓글

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter @ManyToOne(optional = false) // optional하지 않다 = 필수값이다. cascade 댓글을 지웠을때 게시글이 영향을 받으면 안되므로 추가 X
    private Article article;    // 게시글 (ID)

    @Setter @Column(nullable = false, length = 500)
    private String content;     // 본문

    @CreatedDate @Column(nullable = false)
    private LocalDateTime createdAt;    // 생성일자
    @CreatedBy @Column(nullable = false, length = 100)
    private String createdBy;           // 생성자
    @LastModifiedDate @Column(nullable = false)
    private LocalDateTime modifiedAt;   // 수정일자
    @LastModifiedBy @Column(nullable = false)
    private String modifiedBy;          // 수정자

    private ArticleComment(Article article, String content) {
        this.article = article;
        this.content = content;
    }

    public static ArticleComment of(Article article, String content) {
        return new ArticleComment(article, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArticleComment that = (ArticleComment) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
