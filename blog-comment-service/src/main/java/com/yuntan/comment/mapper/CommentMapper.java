package com.yuntan.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuntan.comment.domain.po.Comment;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 根据父评论ID查询子评论
     */
    @Select("select * from comment where parent_id = #{parentId} and status = 1 and deleted = 0 order by create_time desc")
    List<Comment> selectByParentId(Long parentId);

    /**
     * 根据父评论ID列表查询子评论
     */
    List<Comment> selectByParentIds(List<Long> parentIds);

    /**
     * 根据文章ID查询评论数量
     */
    @Select("select count(*) from comment where article_id = #{articleId} and status = 1 and deleted = 0")
    Integer countComments(Long articleId);
}
