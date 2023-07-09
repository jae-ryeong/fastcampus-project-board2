package com.fastcampus.projectboard2.service;

import com.fastcampus.projectboard2.domain.Article;
import com.fastcampus.projectboard2.domain.Hashtag;
import com.fastcampus.projectboard2.domain.UserAccount;
import com.fastcampus.projectboard2.domain.constant.SearchType;
import com.fastcampus.projectboard2.dto.ArticleDto;
import com.fastcampus.projectboard2.dto.ArticleWithCommentsDto;
import com.fastcampus.projectboard2.dto.HashtagDto;
import com.fastcampus.projectboard2.dto.UserAccountDto;
import com.fastcampus.projectboard2.repository.ArticleRepository;
import com.fastcampus.projectboard2.repository.HashtagRepository;
import com.fastcampus.projectboard2.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스 로직 - 게시판")
@ExtendWith(MockitoExtension.class) // Mockito를 사용할 수 있게 해준다.
class ArticleServiceTest {

    @Mock private ArticleRepository articleRepository;
    @Mock private UserAccountRepository userAccountRepository;
    @Mock private HashtagRepository hashtagRepository;
    @Mock private HashtagService hashtagService;

    @InjectMocks private ArticleService sut;

    @DisplayName("검색어 없이 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    public void NoSearchParameters_ArticlePage() throws Exception{
        //given
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findAll(pageable)).willReturn(Page.empty());    // articleRepository에서 findAll메서드를 실행하면 반드지 빈 페이지를 반환하라는 의미
                                                                                // given으로 ~~이렇게하면 ~~이런 결과가 나온다라고 설정해주는것 https://greedy0110.tistory.com/57

        //when
        Page<ArticleDto> articles = sut.searchArticles(null, null, pageable);

        //then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(pageable); // articleRepository가 findAll을 호출했는지 검증
    }

    @DisplayName("검색어 없이 해시태그를 검색하면, 빈 페이지를 반환한다.")
    @Test
    public void givenNoSearchParameters_whenSearchingArticlesViaHashtag_thenReturnsEmptyPage() throws Exception{
        //given
        Pageable pageable = Pageable.ofSize(20);

        //when
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(null, pageable);

        //then
        assertThat(articles).isEqualTo(Page.empty(pageable)); // == .isEmpty()와 같은 의미
        then(articleRepository).shouldHaveNoInteractions(); // 상호작용이 없이 실행됐는지 검증 (왜 이 메서드로 테스트했는지는 아직 모르겠다)
        then(hashtagRepository).shouldHaveNoInteractions();
    }

    @DisplayName("없는 해시태그를 검색하면, 빈 페이지를 반환한다.")
    @Test
    void givenNonexistentHashtag_whenSearchingArticlesViaHashtag_thenReturnsEmptyPage() {
        // Given
        String hashtagName = "난 없지롱";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByHashtagNames(List.of(hashtagName), pageable)).willReturn(new PageImpl<>(List.of(), pageable, 0));

        // When
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(hashtagName, pageable);

        // Then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        then(articleRepository).should().findByHashtagNames(List.of(hashtagName), pageable);
    }

    @DisplayName("게시글을 해시태그를 검색하면, 게시글 페이지를 반환한다.")
    @Test
    public void givenHashtag_whenSearchingArticlesViaHashtag_thenReturnsArticlesPage() throws Exception{
        //given
        String hashtagName = "#java";
        Pageable pageable = Pageable.ofSize(20);
        Article expectedArticle = createArticle();
        given(articleRepository.findByHashtagNames(List.of(hashtagName), pageable)).willReturn(new PageImpl<>(List.of(expectedArticle), pageable, 1));

        //when
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(hashtagName, pageable);

        // Then
        assertThat(articles).isEqualTo(new PageImpl<>(List.of(ArticleDto.from(expectedArticle)), pageable, 1));
        then(articleRepository).should().findByHashtagNames(List.of(hashtagName), pageable);
    }

    @DisplayName("해시태그를 조회하면, 유니크 해시태그 리스트를 반환한다.")
    @Test
    public void givenNothing_whenCalling_thenReturnsHashtags() throws Exception{
        //given
        Article article = createArticle();
        List<String> expectedHashtags = List.of("java", "spring", "boot");
        given(hashtagRepository.findAllHashtagNames()).willReturn(expectedHashtags);

        //when
        List<String> actualHashtags = sut.getHashtags();

        //then
        assertThat(actualHashtags).isEqualTo(expectedHashtags);
        then(hashtagRepository).should().findAllHashtagNames();
    }

    @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    public void SearchArticlesList() throws Exception{
        //given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(20);

        BDDMockito.given(articleRepository.findByTitleContaining(searchKeyword, pageable)).willReturn(Page.empty());

        //when
        Page<ArticleDto> articles =  sut.searchArticles(searchType, searchKeyword, pageable);   // 제목, 본문, ID, 닉네임, 해시태그

        //then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByTitleContaining(searchKeyword, pageable);
    }

    @DisplayName("게시글을 조회하면, 게시글 반환")
    @Test
    public void SearchArticleList() throws Exception{
        //given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));  // findById는 Optional 타입이므로 Optional.of로 받아야한다.

        //when
        ArticleDto dto = sut.getArticle(articleId);

        //then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtagDtos", article.getHashtags().stream()
                        .map(HashtagDto::from)
                        .collect(Collectors.toUnmodifiableSet())
                );

        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글이 없으면, 예외를 던진다.")
    @Test
    public void NoSearchArticle_ThrowException() throws Exception{
        //given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        //when
        Throwable t = catchThrowable(() -> sut.getArticle(articleId));  // service코드에 getArtcile메서드에서 검증부분에 구현할 에러코드가 있다.

        //then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 없습니다 - articleId: " + articleId);
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글 정보를 입력하면, 본문에서 해시태그 정보를 추출하여 해시태그 정보가 포함된 게시글을 생성한다.")
    @Test
    public void givenArticleInfo_whenSavingArticle_thenExtractsHashtagsFromContentAndSavesArticleWithExtractedHashtags() throws Exception{
        //given
        ArticleDto dto = createArticleDto();
        Set<String> expectedHashtagNames = Set.of("java", "spring");
        Set<Hashtag> expectedHashtags = new HashSet<>();
        expectedHashtags.add(createHashtag("java"));

        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(createUserAccount());
        given(articleRepository.save(any(Article.class))).willReturn(createArticle());  // Article.class아무거나를 save하면 createArticle()메서드를 실행해 테스트 예제를 만들어준다.
        given(hashtagService.parseHashtagNames(dto.content())).willReturn(expectedHashtagNames);
        given(hashtagService.findHashtagsByNames(expectedHashtagNames)).willReturn(expectedHashtags);
        //when
        sut.saveArticle(dto);   // dto를 매개변수로 넣어주면 service 코드에서 entity로 변환시켜주고 저장된다.

        //then
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(articleRepository).should().save(any(Article.class));
        then(hashtagService).should().parseHashtagNames(dto.content());
        then(hashtagService).should().findHashtagsByNames(expectedHashtagNames);
    }

    @DisplayName("게시글의 수정 정보를 입력하면, 게시글을 수정한다.")
    @Test
    public void givenModifiedArticleInfo_whenUpdatingArticle_thenUpdatesArticle() throws Exception{
        //given
        Article article = createArticle();
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용 #springboot");
        Set<String> expectedHashtagNames = Set.of("springboot");
        Set<Hashtag> expectedHashtags = new HashSet<>();

        given(articleRepository.getReferenceById(dto.id())).willReturn(article);
        // getReferenceById는 findById와 비슷하지만 내부 동작이 다르다.
        // 단건 조회의 경우 findById
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(dto.userAccountDto().toEntity());
        willDoNothing().given(articleRepository).flush();
        willDoNothing().given(hashtagService).deleteHashtagWithoutArticles(any());
        given(hashtagService.parseHashtagNames(dto.content())).willReturn(expectedHashtagNames);
        given(hashtagService.findHashtagsByNames(expectedHashtagNames)).willReturn(expectedHashtags);

        //when
        sut.updateArticle(dto.id(), dto);

        //then
        assertThat(article)
                .hasFieldOrPropertyWithValue("title", dto.title())
                .hasFieldOrPropertyWithValue("content", dto.content())
                .extracting("hashtags", as(InstanceOfAssertFactories.COLLECTION))
                .hasSize(1)
                .extracting("hashtagName")
                .containsExactly("springboot");

        then(articleRepository).should().getReferenceById(dto.id());
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(articleRepository).should().flush();
        then(hashtagService).should(times(2)).deleteHashtagWithoutArticles(any());
        then(hashtagService).should().parseHashtagNames(dto.content());
        then(hashtagService).should().findHashtagsByNames(expectedHashtagNames);
    }

    @DisplayName("없는 게시글의 수정 정보를 입력하면, 경고 로그를 찍고 아무 것도 하지 않는다.")
    @Test
    public void givenNonexistentArticleInfo_whenUpdatingArticle_thenLogsWarningAndDoesNothing() throws Exception{
        //given
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용");
        given(articleRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);   // willThrow로 경고를 발생시킨다.

        //when
        sut.updateArticle(dto.id(), dto); // 경고로그는 이곳에 있다.

        //then
        then(articleRepository).should().getReferenceById(dto.id());
        then(userAccountRepository).shouldHaveNoInteractions();
        then(hashtagService).shouldHaveNoInteractions();
    }

    @DisplayName("게시글의 ID를 입력하면, 게시글을 삭제한다")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() throws Exception{
        //given
        long articleId = 1L;
        String userId = "wofud";
        given(articleRepository.getReferenceById(articleId)).willReturn(createArticle());
        willDoNothing().given(articleRepository).deleteByIdAndUserAccount_UserId(articleId, userId); // willDoNothing은 void return타입에 대응하기 위해 사용
        willDoNothing().given(articleRepository).flush();
        willDoNothing().given(hashtagService).deleteHashtagWithoutArticles(any());

        //when
        sut.deleteArticle(articleId, userId);

        //then
        then(articleRepository).should().getReferenceById(articleId);
        then(articleRepository).should().deleteByIdAndUserAccount_UserId(articleId, userId); // save를 한번은 호출했는가 검사
        then(articleRepository).should().flush();
        then(hashtagService).should(times(2)).deleteHashtagWithoutArticles(any());
    }

    @DisplayName("게시글 작성자가 아닌 사람이 수정 정보를 입력하면, 아무 것도 하지 않는다.")
    @Test
    void givenModifiedArticleInfoWithDifferentUser_whenUpdatingArticle_thenDoesNothing() {
        // Given
        Long differentArticleId = 22L;
        Article differentArticle = createArticle(differentArticleId);
        differentArticle.setUserAccount(createUserAccount("John"));
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용");
        given(articleRepository.getReferenceById(differentArticleId)).willReturn(differentArticle);
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(dto.userAccountDto().toEntity());

        // When
        sut.updateArticle(differentArticleId, dto);

        // Then
        then(articleRepository).should().getReferenceById(differentArticleId);
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(hashtagService).shouldHaveNoInteractions();
    }

    @DisplayName("게시글 수를 조회하면, 게시글 수를 반환한다.")
    @Test
    public void givenNothing_whenCountingArticles_thenReturnsArticleCount() throws Exception{
        //given
        long expected = 0L;
        given(articleRepository.count()).willReturn(expected);

        //when
        long actual = sut.getArticleCount();

        //then
        assertThat(actual).isEqualTo(expected);
        then(articleRepository).should().count();
    }

    @DisplayName("게시글 ID로 조회하면, 댓글 달긴 게시글을 반환한다.")
    @Test
    void givenArticleId_whenSearchingArticleWithComments_thenReturnsArticleWithComments() {
        //given
        Long articleId = 1L;
        Article article = createArticle();

        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        //when
        ArticleWithCommentsDto dto = sut.getArticleWithComments(articleId);

        //then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtagDtos", article.getHashtags().stream()
                        .map(HashtagDto::from)
                        .collect(Collectors.toUnmodifiableSet())
                );
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("댓글 달린 게시글이 없으면, 예외를 던진다.")
    @Test
    void givenNonexistentArticleId_whenSearchingArticleWithComments_thenThrowsException() {
        //given
        Long articleId = 1L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        //when
        Throwable t = catchThrowable(() -> sut.getArticleWithComments(articleId));

        //then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 없습니다 - articleId: " + articleId);
        then(articleRepository).should().findById(articleId);
    }

    private UserAccount createUserAccount() {
        return createUserAccount("wofud");
    }
    private UserAccount createUserAccount(String userId) {
        return UserAccount.of(
                userId,
                "password",
                "wofud0321@naver.com",
                "wofud",
                "memo"
        );
    }

    private Article createArticle() {
        return createArticle(1L);
    }

    private Article createArticle(Long id) {
        Article article = Article.of(
                createUserAccount(),
                "title",
                "content"
        );
        article.addHashtags(Set.of(
                createHashtag(1L, "java"),
                createHashtag(2L, "spring")
        ));
        ReflectionTestUtils.setField(article, "id", id);

        return article;
    }

    private Hashtag createHashtag(String hashtagName) {
        return createHashtag(1L, hashtagName);
    }

    private Hashtag createHashtag(Long id, String hashtagName) {
        Hashtag hashtag = Hashtag.of(hashtagName);
        ReflectionTestUtils.setField(hashtag, "id", id);

        return hashtag;
    }

    private HashtagDto createHashtagDto() {
        return HashtagDto.of("java");
    }

    private ArticleDto createArticleDto() {
        return createArticleDto("title", "content");
    }

    private ArticleDto createArticleDto(String title, String content) {
        return ArticleDto.of(1L, createUserAccountDto(), title, content, null, LocalDateTime.now(), "wofud", LocalDateTime.now(), "wofud");
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "wofud", "password", "wofud0321@naver.com", "wofud", "memo",
                LocalDateTime.now(), "wofud", LocalDateTime.now(), "wofud"
        );
    }
    
    
}