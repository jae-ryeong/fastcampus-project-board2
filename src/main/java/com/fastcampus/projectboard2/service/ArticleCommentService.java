package com.fastcampus.projectboard2.service;

import com.fastcampus.projectboard2.domain.Article;
import com.fastcampus.projectboard2.domain.ArticleComment;
import com.fastcampus.projectboard2.domain.UserAccount;
import com.fastcampus.projectboard2.dto.ArticleCommentDto;
import com.fastcampus.projectboard2.repository.ArticleCommentRepository;
import com.fastcampus.projectboard2.repository.ArticleRepository;
import com.fastcampus.projectboard2.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class ArticleCommentService {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public List<ArticleCommentDto> searchArticleComments(Long articleId) {
        return articleCommentRepository.findByArticle_Id(articleId)
                .stream().map(ArticleCommentDto::from)
                .toList();
    }

    public void saveArticleComment(ArticleCommentDto dto) {
        try {
            Article article = articleRepository.getReferenceById(dto.articleId());  // 게시글에 대한 정보
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId()); // 댓글의 댓글 작성자
            articleCommentRepository.save(dto.toEntity(article, userAccount));
            // 매개변수 dto를 ArticleComment Entity로 만들어서 저장
            // Article referenceById = articleRepository.getReferenceById(dto.articleId());
            // getReferenceById는 Id가 없을시 EntityNotFountException을 발생시킨다.
        } catch (EntityNotFoundException e) {
            log.warn("댓글 저장 실패. 댓글작성에 필요헌 정보를 찾을 수 없습니다 - dto: {}", e.getLocalizedMessage());
        }
    }

    public void updateArticleComment(ArticleCommentDto dto) {
        try {
            ArticleComment articleComment = articleCommentRepository.getReferenceById(dto.articleId());
            if(dto.content() != null){
                articleComment.setContent(dto.content());
            }

        } catch (EntityNotFoundException e) {
            log.warn("댓글 업데이트 실패, 댓글을 찾을 수 없습니다 - dto: {}", dto);
        }
    }

    public void deleteArticleComment(Long articleCommentId) {
        articleCommentRepository.deleteById(articleCommentId);
    }
}
