package com.fastcampus.projectboard2.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
@Entity
public class Article extends AuditingFields{

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
