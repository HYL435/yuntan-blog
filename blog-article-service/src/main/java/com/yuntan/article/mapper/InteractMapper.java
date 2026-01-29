package com.yuntan.article.mapper;

import com.yuntan.article.domain.po.ArticleCollect;
import com.yuntan.article.domain.po.ArticleLike;
import com.yuntan.article.domain.po.ArticleView;
import org.apache.ibatis.annotations.Delete;
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

    /**
     * 删除文章收藏
     */
    @Delete("delete from article_collect where article_id = #{articleId} and user_id = #{currentId}")
    void deleteCollect(Long articleId, Long currentId);

    /**
     * 删除文章点赞
     */
    @Delete("delete from article_like where article_id = #{articleId} and user_id = #{currentId}")
    void deleteLike(Long articleId, Long currentId);

    /**
     * 添加文章浏览
     */
    @Insert("insert into article_view (user_id, article_id, ip, create_time) values (#{userId}, #{articleId},#{ip}, #{createTime})")
    void addView(ArticleView view);
}
