package com.xingmao.controller;

import com.xingmao.entity.SysUser;
import com.xingmao.service.UserService;
import com.xingmao.utils.JwtUtils;
import com.xingmao.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> loginReq) {
        String username = loginReq.get("username");
        String password = loginReq.get("password");
        String loginType = loginReq.getOrDefault("loginType", "user");
        Map<String, Object> result = userService.login(username, password, loginType);
        return Result.success(result);
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody SysUser user) {
        userService.register(user);
        return Result.success("注册成功");
    }

    @GetMapping("/info")
    public Result<?> getUserInfo(Authentication auth) {
        if (auth == null) {
            return Result.unauthorized("请先登录");
        }
        Long userId = (Long) auth.getCredentials();
        SysUser user = userService.getCurrentUser(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @GetMapping("/list")
    public Result<?> getUserList() {
        return Result.success(userService.list());
    }

    @PutMapping
    public Result<?> updateUser(@RequestBody SysUser user) {
        userService.updateUser(user);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除成功");
    }
}