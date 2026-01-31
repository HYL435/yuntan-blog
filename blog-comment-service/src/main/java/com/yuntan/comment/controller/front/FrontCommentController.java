package com.yuntan.comment.controller.front;

import com.yuntan.comment.domain.dto.front.CommentDTO;
import com.yuntan.comment.domain.vo.front.CommentVO;
import com.yuntan.comment.service.ICommentService;
import com.yuntan.common.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/front/comments")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "前台-评论接口")
public class FrontCommentController {

    public final ICommentService commentService;

    @PostMapping
    @Operation(summary = "添加评论")
    public Result<Void> saveComment(@RequestBody CommentDTO commentDTO) {

        log.info("保存评论，评论参数：{}", commentDTO);

        commentService.saveComment(commentDTO);

        return Result.ok();
    }


    @PostMapping("/{articleId}")
    @Operation(summary = "查询文章下的评论")
    public Result<List<CommentVO>> listComments(@PathVariable Long articleId) {

        log.info("查询文章下的评论，文章ID：{}", articleId);

        List<CommentVO> commentVOS = commentService.listComments(articleId);

        return Result.ok(commentVOS);
    }

}
