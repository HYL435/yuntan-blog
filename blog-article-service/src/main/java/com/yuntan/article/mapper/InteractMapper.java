package com.yuntan.article.mapper;

import com.yuntan.article.domain.po.ArticleCollect;
import com.yuntan.article.domain.po.ArticleLike;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface InteractMapper {

    /**
     * 判断用户是否点赞
     */
    @Select("select count(*) from article_like where article_id = #{articleId} and user_id = #{userId}")
    int isLike(Long articleId, Long userId);

    /**
     * 判断用户是否收藏
     */
    @Select("select count(*) from article_collect where article_id = #{articleId} and user_id = #{userId}")
    int isCollect(Long id, Long userId);

    /**
     * 文章点赞
     */
    @Insert("insert into article_like (user_id, article_id, create_time) values (#{userId}, #{articleId}, #{createTime})")
    void like(ArticleLike like);

    /**
     * 添加文章收藏
     */
    @Insert("insert into article_collect (user_id, article_id, create_time) values (#{userId}, #{articleId}, #{createTime})")
    void collect(ArticleCollect collect);
}
