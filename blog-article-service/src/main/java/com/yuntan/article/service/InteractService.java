package com.yuntan.article.service;


public interface InteractService {

    /**
     * 文章点赞
     */
    void like(Long articleId);

    /**
     * 添加文章收藏
     */
    void collect(Long articleId);
}
