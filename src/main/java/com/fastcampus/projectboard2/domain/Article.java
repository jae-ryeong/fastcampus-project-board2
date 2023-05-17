package com.fastcampus.projectboard2.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Article {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter @Column(nullable = false)
    private String title;   // 제목
    @Setter @Column(nullable = false, length = 10000)
    private String content; // 본문
    @Setter
    private String hashtag; // 해시태그

    @OrderBy("id")  // id로 정렬
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL )
    @ToString.Exclude   // @ToString의 성능 이슈저하때문에 추가 ,데이터베이스 접근로직3 20분 20초
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();
    // Set Comment를 중복을 허용하지 않고 Collection으로 보겠다.

    @CreatedDate @Column(nullable = false)
    private LocalDateTime createdAt;    // 생성일자
    @CreatedBy @Column(nullable = false, length = 100)
    private String createdBy;           // 생성자
    @LastModifiedDate @Column(nullable = false)
    private LocalDateTime modifiedAt;   // 수정일자
    @LastModifiedBy @Column(nullable = false)
    private String modifiedBy;          // 수정자

    protected Article() {}

    private Article(String title, String content, String hashtag) {
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    public static Article of(String title, String content, String hashtag) {    // 팩토리 메서드 패턴인듯??
        return new Article(title, content, hashtag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return id != null && id.equals(article.id); // 아직 영속화 되지 않은 id들은 내용 해시태그등 모든게 똑같아도 다르게 취급하겠다는 의미 (데이터베이스 접근로직2 23분 정도)
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
