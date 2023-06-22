package com.fastcampus.projectboard2.service;

import com.fastcampus.projectboard2.domain.Article;
import com.fastcampus.projectboard2.domain.ArticleComment;
import com.fastcampus.projectboard2.domain.UserAccount;
import com.fastcampus.projectboard2.dto.ArticleCommentDto;
import com.fastcampus.projectboard2.dto.UserAccountDto;
import com.fastcampus.projectboard2.repository.ArticleCommentRepository;
import com.fastcampus.projectboard2.repository.ArticleRepository;
import com.fastcampus.projectboard2.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스 로직 - 댓글")
@ExtendWith(MockitoExtension.class) // Mockito를 사용할 수 있게 해준다.
class ArticleCommentServiceTest {

    @InjectMocks private ArticleCommentService sut;
    @Mock private ArticleRepository articleRepository;
    @Mock private ArticleCommentRepository articleCommentRepository;
    @Mock private UserAccountRepository userAccountRepository;

    @DisplayName("게시글 ID로 조회하면, 해당하는 댓글 리스트를 반환한다.")
    @Test
    public void IdFind_ListReturn() throws Exception{
        //given
        Long articleId = 1L;
        ArticleComment expected = createArticleComment("content");
        given(articleCommentRepository.findByArticle_Id(articleId)).willReturn(List.of(expected));

        //when
        List<ArticleCommentDto> actual = sut.searchArticleComments(articleId);

        //then
        assertThat(actual).hasSize(1).first().hasFieldOrPropertyWithValue("content", expected.getContent());
        then(articleCommentRepository).should().findByArticle_Id(articleId);
    }

    @DisplayName("댓글 정보를 입력하면, 댓글을 저장한다.")
    @Test
    public void Comments_save() throws Exception{
        //given
        ArticleCommentDto dto = createArticleCommentDto("댓글");  // 댓글 DTO 생성

        given(articleRepository.getReferenceById(dto.articleId())).willReturn(createArticle());
        given(articleCommentRepository.save(any(ArticleComment.class))).willReturn(null);
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(createUserAccount());   // 댓글을 쓴 유저의 ID로 유저를 찾아보면 createUserAccount()가 반환된다.
        //when
        sut.saveArticleComment(dto);
        //then
        then(articleRepository).should().getReferenceById(dto.articleId());
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(articleCommentRepository).should().save(any(ArticleComment.class));
    }

    @DisplayName("댓글 저장을 시도했는데 맞는 게시글이 없으면, 경고 로그를 찍고 아무것도 안 한다.")
    @Test
    public void SaveArticle_ThenLog() throws Exception{
        //given
        ArticleCommentDto dto = createArticleCommentDto("댓글");

        given(articleRepository.getReferenceById(dto.articleId())).willThrow(EntityNotFoundException.class);

        //when
        sut.saveArticleComment(dto);

        //then
        then(articleRepository).should().getReferenceById(dto.articleId());
        then(userAccountRepository).shouldHaveNoInteractions(); // userAccountRepository는 아무런 Interaction도 하지 않았다.
        then(articleCommentRepository).shouldHaveNoInteractions();
    }

    @DisplayName("댓글 정보를 입력하면, 댓글을 수정한다.")
    @Test
    public void UpdateComments() throws Exception{
        //given
        String oldContent = "content";
        String updatedContent = "댓글";
        ArticleComment articleComment = createArticleComment(oldContent);
        ArticleCommentDto dto = createArticleCommentDto(updatedContent);

        given(articleCommentRepository.getReferenceById(dto.id())).willReturn(articleComment);

        //when
        sut.updateArticleComment(dto);

        //then
        assertThat(articleComment.getContent())
                .isNotEqualTo(oldContent)
                .isEqualTo(updatedContent);
        then(articleCommentRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("없는 댓글 정보를 수정하려고 하면, 경고 로그를 찍고 아무 것도 안 한다.")
    @Test
    public void NotUpdateComments_Log() throws Exception{
        //given
        ArticleCommentDto dto = createArticleCommentDto("댓글");

        given(articleCommentRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);
        //when
        sut.updateArticleComment(dto);

        //then
        then(articleCommentRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("댓글 ID를 입력하면, 댓글을 삭제한다.")
    @Test
    public void Comment_Id_Delete() throws Exception{
        //given
        Long articleCommentId = 1L;
        String userId = "wofud";
        willDoNothing().given(articleCommentRepository).deleteByIdAndUserAccount_UserId(articleCommentId, userId);

        //when
        sut.deleteArticleComment(articleCommentId, userId);

        //then
        then(articleCommentRepository).should().deleteByIdAndUserAccount_UserId(articleCommentId, userId);
    }

    private ArticleCommentDto createArticleCommentDto(String content) {
        return ArticleCommentDto.of(1L, 1L, createUserAccountDto(), content, LocalDateTime.now(), "wofud", LocalDateTime.now(), "wofud");
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
            "wofud", "password", "wofud0321@naver.com", "wofud", "memo", LocalDateTime.now(), "wofud", LocalDateTime.now(), "wofud"
        );
    }

    private ArticleComment createArticleComment(String content) {
        return ArticleComment.of(
                Article.of(createUserAccount(), "title", "content", "hashtag"),
                createUserAccount(),
                content
        );
    }

    private UserAccount createUserAccount() {
        return UserAccount.of("wofud","password","wofud0321@naver.com","wofud",null);
    }

    private Article createArticle() {
        return Article.of(createUserAccount(), "title", "content", "#java");
    }
}