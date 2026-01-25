package com.yuntan.user.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuntan.common.domain.PageDTO;
import com.yuntan.common.domain.Result;
import com.yuntan.user.domain.po.User;
import com.yuntan.user.domain.query.UserPageQuery;
import com.yuntan.user.domain.vo.admin.AdminUserVO;
import com.yuntan.user.service.IUserService;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@Tag(description = "后台用户相关接口", name = "用户管理")
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final IUserService userService;

    @Schema(description = "分页查询用户列表")
    @GetMapping("/page")
    public Result<PageDTO<AdminUserVO>> pageUser(UserPageQuery userPageQuery) {

        log.info("分页查询用户列表，查询参数：{}", userPageQuery);

        Page<User> result = userService.page(userPageQuery.toMpPage(userPageQuery.getSortBy(), userPageQuery.getIsAsc()));

        return Result.ok(PageDTO.of(result, AdminUserVO.class));
    }

    @Schema(description = "批量删除用户")
    @PutMapping("/delete/{id}")
    public Result<Void> deleteUsers(@PathVariable Long id) {
        log.info("批量删除用户，用户ID列表：{}", id);

        userService.removeById(id);

        return Result.ok();
    }

    @Schema(description = "启用禁用账号")
    @PutMapping("/status")
    public Result<Void> updateUserStatus(@RequestParam Long id, Integer status) {
        log.info("启用禁用账号，用户ID：{}，状态：{}", id, status);

        userService.enableOrDisableUser(id, status);

        return Result.ok();
    }

    @Schema(description = "提升用户为管理员")
    @PutMapping("/upgrade/{id}")
    public Result<Void> upgradeAdmin(@PathVariable Long id) {
        log.info("提升用户为管理员，用户ID：{}", id);

        userService.upgradeAdmin(id);

        return Result.ok();
    }

}
