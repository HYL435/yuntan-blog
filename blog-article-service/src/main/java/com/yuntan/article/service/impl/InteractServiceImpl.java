package com.yuntan.article.service.impl;

import com.yuntan.article.domain.po.ArticleCollect;
import com.yuntan.article.domain.po.ArticleLike;
import com.yuntan.article.mapper.InteractMapper;
import com.yuntan.article.service.InteractService;
import com.yuntan.common.context.BaseContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InteractServiceImpl implements InteractService {

    private final InteractMapper interactMapper;


    /**
     * 文章点赞
     */
    @Override
    public void like(Long articleId) {

        ArticleLike like = ArticleLike.builder()
                .articleId(articleId)
                .userId(BaseContext.getCurrentId())
                .createTime(LocalDateTime.now())
                .build();


        interactMapper.like(like);

    }

    /**
     * 文章收藏
     */
    @Override
    public void collect(Long articleId) {

        ArticleCollect collect = ArticleCollect.builder()
                .articleId(articleId)
                .userId(BaseContext.getCurrentId())
                .createTime(LocalDateTime.now())
                .build();

        interactMapper.collect(collect);
    }
}
