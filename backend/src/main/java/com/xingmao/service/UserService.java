package com.xingmao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xingmao.entity.SysUser;

import java.util.Map;

public interface UserService extends IService<SysUser> {

    Map<String, Object> login(String username, String password, String loginType);

    void register(SysUser user);

    SysUser getCurrentUser(Long userId);

    void updateUser(SysUser user);

    void deleteUser(Long id);
}