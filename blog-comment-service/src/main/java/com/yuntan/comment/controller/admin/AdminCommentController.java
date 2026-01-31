package com.yuntan.comment.controller.admin;

import com.yuntan.comment.service.ICommentService;
import com.yuntan.common.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
@Tag(name = "后台-评论接口")
public class AdminCommentController  {

    public final ICommentService commentService;

    @Operation(summary = "删除评论")
    @RequestMapping("/deleted/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {

        log.info("删除评论，评论ID：{}", id);

        commentService.removeById(id);

        return Result.ok();
    }

}
