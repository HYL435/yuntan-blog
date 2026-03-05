package com.yuntan.comment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuntan.comment.domain.dto.admin.CommentStatusDTO;
import com.yuntan.comment.domain.dto.front.CommentDTO;
import com.yuntan.comment.domain.po.Comment;
import com.yuntan.comment.domain.vo.front.CommentVO;
import com.yuntan.common.domain.PageQuery;

import java.util.List;

public interface ICommentService extends IService<Comment> {

    /**
     * 添加评论
     */
    void saveComment(CommentDTO commentDTO);

    /**
     * 获取评论列表
     */
    List<CommentVO> listComments(Long articleId);

    /**
     * 获取评论数量
     */
    Integer countComments(Long articleId);

    /**
     * 后台分页查询评论列表
     */
    Page<Comment> listCommentsAdmin(PageQuery pageQuery);

    /**
     * 修改评论状态
     */
    void updateStatusById(CommentStatusDTO commentStatusDTO);
}
