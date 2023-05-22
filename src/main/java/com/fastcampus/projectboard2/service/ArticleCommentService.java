package com.fastcampus.projectboard2.service;

import com.fastcampus.projectboard2.dto.ArticleCommentDto;
import com.fastcampus.projectboard2.dto.ArticleDto;
import com.fastcampus.projectboard2.repository.ArticleCommentRepository;
import com.fastcampus.projectboard2.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleCommentService {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    @Transactional(readOnly = true)
    public List<ArticleCommentDto> searchArticleComments(Long articleId) {
        return List.of();
    }

    public void saveArticleComment(ArticleCommentDto dto) {
    }

    public void updateArticleComment(ArticleCommentDto dto) {

    }

    public void deleteArticleComment(Long articleCommentId) {

    }
}
