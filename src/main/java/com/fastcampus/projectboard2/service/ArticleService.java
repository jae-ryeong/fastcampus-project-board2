package com.fastcampus.projectboard2.service;

import com.fastcampus.projectboard2.domain.Article;
import com.fastcampus.projectboard2.domain.type.SearchType;
import com.fastcampus.projectboard2.dto.ArticleDto;
import com.fastcampus.projectboard2.dto.ArticleWithCommentsDto;
import com.fastcampus.projectboard2.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) { // 게시글 페이지 반환

        if (searchKeyword == null || searchKeyword.isBlank()) {
            return articleRepository.findAll(pageable).map(ArticleDto::from);   // 검색창이 비어있으면 게시글페이지 전체를 반환
        }

        return switch (searchType){
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(entity -> ArticleDto.from(entity));  // 메서드 참조를 람다로 바꿔보았다.
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from);
            case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDto::from);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::from);
            case HASHTAG -> articleRepository.findByHashtag("#" + searchKeyword, pageable).map(ArticleDto::from);
        };
    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticle(long articleId) {  // 단건 조회, ArticleWithCommentsDto는 ArticleDto + ArticleCommentsDto이며 이유는 게시글을 불러오면 그 댓글들도 모두 가져오기 위해서이다.
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentsDto::from)  // 여기까지는 (findById가)Optional이기 때문에 까줘야한다. (보통 get으로)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다.")); // 만약 에러가 발생할경우, 그리고 여기서 Optional을 까준거 같다 ㅋㅋ..
    }

    public void saveArticle(ArticleDto dto) {
        articleRepository.save(dto.toEntity()); // dto를 entity로 변환시켜준 후 저장한다.
    }

    public void updateArticle(ArticleDto dto) {
        try {
            Article article = articleRepository.getReferenceById(dto.id()); // getReferenceById() 메서드를 사용하여 엔티티 생성에 프록시 객체를 넣어주었다. 따라서 select 문이 제거
                                                                            // 프록시만 반환, 사용하기 전까지 실제 DB 접근 X, 그렇기에 성능면에서 findbyid보다 유리
            if (dto.title() != null) {article.setTitle(dto.title());}
            if (dto.content() != null) {article.setContent(dto.content());}

            article.setHashtag(dto.hashtag());  // null값 허용이므로 if문이 필요없다
        } catch (EntityNotFoundException e){
            log.warn("게시글 업데이트 실패. 게시글을 찾을 수 없습니다 - dto: {}", dto); // {}이거는 string 인터폴레이션 기법
        }
    }

    public void deleteArticle(long articleId) {
        articleRepository.deleteById(articleId);
    }

    public long getArticleCount() {
        return articleRepository.count();   // Article Entity의 id가 몇 개인지, 전체 게시글이 몇 개인지 반환
    }   
}
