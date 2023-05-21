package com.fastcampus.projectboard2.service;

import com.fastcampus.projectboard2.repository.ArticleCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleCommentService {

    private final ArticleCommentRepository articleCommentRepository;

    public ArticleCommentService(@Autowired ArticleCommentRepository articleCommentRepository) {
        this.articleCommentRepository = articleCommentRepository;
    }
}
