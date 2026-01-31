package com.yuntan.comment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuntan.comment.domain.dto.front.CommentDTO;
import com.yuntan.comment.domain.po.Comment;
import com.yuntan.comment.domain.vo.front.CommentVO;

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
}
