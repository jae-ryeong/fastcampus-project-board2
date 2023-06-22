package com.fastcampus.projectboard2.service;

import com.fastcampus.projectboard2.domain.Article;
import com.fastcampus.projectboard2.domain.UserAccount;
import com.fastcampus.projectboard2.domain.constant.SearchType;
import com.fastcampus.projectboard2.dto.ArticleDto;
import com.fastcampus.projectboard2.dto.ArticleWithCommentsDto;
import com.fastcampus.projectboard2.repository.ArticleRepository;
import com.fastcampus.projectboard2.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;

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
    public ArticleWithCommentsDto getArticleWithComments(long articleId) {  // 단건 조회, ArticleWithCommentsDto는 ArticleDto + ArticleCommentsDto이며 이유는 게시글을 불러오면 그 댓글들도 모두 가져오기 위해서이다.
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentsDto::from)  // 여기까지는 (findById가)Optional이기 때문에 까줘야한다. (보통 get으로)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId)); // 만약 에러가 발생할경우, 그리고 여기서 Optional을 까준거 같다 ㅋㅋ..
    }

    @Transactional(readOnly = true)
    public ArticleDto getArticle(Long articleId) {  // article만 가져와준다. 댓글 없이 게시글만
        return articleRepository.findById(articleId)
                .map(ArticleDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }

    public void saveArticle(ArticleDto dto) {
        UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());    // 게시글을 쓴 유저의id를 받아와 유저의 계정정보를 담고있는 변수생성
        articleRepository.save(dto.toEntity(userAccount)); // 계정정보와, title, content, hashtag만 Entity로 바꾸어 저장한다.
    }

    public void updateArticle(Long articleId, ArticleDto dto) {
        try {
            /*Article article = articleRepository.getReferenceById(dto.id()); // getReferenceById() 메서드를 사용하여 엔티티 생성에 프록시 객체를 넣어주었다. 따라서 select 문이 제거
                                                                            // 프록시만 반환, 사용하기 전까지 실제 DB 접근 X, 그렇기에 성능면에서 findbyid보다 유리
                                                                            // null은 에러를 일으킨다.*/
            Article article = articleRepository.getReferenceById(articleId);    // 게시글 id
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());    // 게시글을 쓴 유저의 id

            if(article.getUserAccount().equals(userAccount)) { // 게시글 사용자와 인증된 사용자가 동일인인지 검사
                if (dto.title() != null) {
                    article.setTitle(dto.title());
                }
                if (dto.content() != null) {
                    article.setContent(dto.content());
                }
            }
            article.setHashtag(dto.hashtag());  // null값 허용이므로 if문이 필요없다
        } catch (EntityNotFoundException e){
            log.warn("게시글 업데이트 실패. 게시글을 수정하는데 필요한 정보를 찾을 수 없습니다 - {}", e.getLocalizedMessage()); // {}이거는 string 인터폴레이션 기법
        }
    }

    public void deleteArticle(long articleId, String userId) {
        articleRepository.DeleteByIdAndUserAccount_UserId(articleId, userId);
    }

    public long getArticleCount() {
        return articleRepository.count();   // Article Entity의 id가 몇 개인지, 전체 게시글이 몇 개인지 반환
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticlesViaHashtag(String hashtag, Pageable pageable) {
        if (hashtag == null || hashtag.isBlank()) {
            return Page.empty(pageable);
        }

        return articleRepository.findByHashtag(hashtag, pageable)
                .map(ArticleDto::from);
    }

    public List<String> getHashtags() { // 해시태그를 중복 값 삭제 후 리스트로 반환
        return articleRepository.findAllDistinctHashtags();
    }
}
