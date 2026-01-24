package com.yuntan.user.controller.front;

import com.yuntan.common.domain.Result;
import com.yuntan.user.domain.dto.front.*;
import com.yuntan.user.domain.vo.front.UserLoginVO;
import com.yuntan.user.domain.vo.front.UserVO;
import com.yuntan.user.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(description = "前台用户相关接口", name = "用户管理")
@RestController
@RequestMapping("/front/users")
@RequiredArgsConstructor
public class FrontUserController {

    private final IUserService userService;


    @Operation(summary = "用户注册", description = "包括用户名，密码，邮箱")
    @PostMapping("/register")
    public Result<UserLoginVO> registerUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        log.info("用户注册: {}", userRegisterDTO);

        UserLoginVO userLoginVO = userService.registerUser(userRegisterDTO);

        return Result.ok(userLoginVO);
    }

    @Operation(summary = "用户登录", description = "包括用户名，密码")
    @PostMapping("/login")
    public Result<UserLoginVO> loginUser(@RequestBody UserLoginDTO userLoginDTO) {

        log.info("用户登录: {}", userLoginDTO);

        UserLoginVO userLoginVO = userService.loginUser(userLoginDTO);

        return Result.ok(userLoginVO);
    }

    @Operation(summary = "根据用户ID获取用户信息", description = "包括用户名，昵称，邮箱等基本信息")
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable Long id) {

        log.info("根据用户ID获取用户信息");

        UserVO userVO = userService.getUserById(id);

        return Result.ok(userVO);
    }

    @Operation(summary = "修改用户信息", description = "修改用户的基本信息")
    @PutMapping
    public Result<Void> reviseUserInfo(@RequestBody UserDTO userDTO) {
        log.info("修改用户信息: {}", userDTO);

        userService.reviseUserInfo(userDTO);

        return  Result.ok();
    }

    @Operation(summary = "修改用户密码", description = "修改用户的登录密码")
    @PutMapping("/editPassword")
    public Result<Void> reviseUserPassword(@RequestBody UpdateUserPwdDTO updateUserPwdDTO) {
        log.info("修改用户密码: {}", updateUserPwdDTO);

        userService.reviseUserPassword(updateUserPwdDTO);

        return Result.ok();
    }

    @Operation(summary = "忘记密码", description = "用户忘记密码后的处理")
    @PutMapping("/forgetPassword")
    public Result<Void> forgetUserPassword(@RequestBody ForgetUserPwdDTO forgetUserPwdDTO) {
        log.info("忘记密码: {}", forgetUserPwdDTO);

        userService.forgetUserPassword(forgetUserPwdDTO);

        return Result.ok();
    }

    @Operation(summary = "用户登出", description = "用户退出登录")
    @PostMapping("/logout")
    public Result<Void> logoutUser(HttpServletRequest request) {
        log.info("用户登出");

        userService.logoutUser(request);

        return Result.ok();
    }
}
