package com.fastcampus.projectboard2.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true) // auditingfield까지 출력이된다.
@Table(
        indexes = {
                @Index(columnList = "hashtagName", unique = true),
                @Index(columnList = "createdAt"),
                @Index(columnList = "createdBy")
        })
@Entity
public class Hashtag extends AuditingFields{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude   // 순환 참조를 막기 위해 (ToString이 계속 출력되는걸 막는다)
    @ManyToMany(mappedBy = "hashtags")
    private Set<Article> articles = new LinkedHashSet<>();   // 순서가 중요하면 LinkedHashSet 중요치 않다면 HashSet

    @Setter
    @Column(nullable = false)
    private String hashtagName; // 해시태그 이름

    protected Hashtag() {}

    private Hashtag(String hashtagName) {
        this.hashtagName = hashtagName;
    }
    public static Hashtag of(String hashtagName) {
        return new Hashtag(hashtagName);    // 팩토리 메서드 패턴
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hashtag that = (Hashtag) o;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
