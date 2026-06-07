package com.xingmao.controller;

import com.xingmao.entity.CustomerAddress;
import com.xingmao.service.AddressService;
import com.xingmao.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    public Result<?> getMyAddresses(Authentication auth) {
        if (auth == null) {
            return Result.unauthorized("请先登录");
        }
        Long userId = (Long) auth.getCredentials();
        List<CustomerAddress> list = addressService.getListByUserId(userId);
        return Result.success(list);
    }

    @PostMapping
    public Result<?> addAddress(@RequestBody CustomerAddress address, Authentication auth) {
        if (auth == null) {
            return Result.unauthorized("请先登录");
        }
        Long userId = (Long) auth.getCredentials();
        address.setUserId(userId);
        addressService.addAddress(address);
        return Result.success("添加成功");
    }

    @PutMapping("/{id}")
    public Result<?> updateAddress(@PathVariable Long id, @RequestBody CustomerAddress address, Authentication auth) {
        if (auth == null) {
            return Result.unauthorized("请先登录");
        }
        Long userId = (Long) auth.getCredentials();
        // 校验归属
        CustomerAddress existing = addressService.getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            return Result.error(403, "无权修改此地址");
        }
        address.setId(id);
        address.setUserId(userId);
        addressService.updateAddress(address);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteAddress(@PathVariable Long id, Authentication auth) {
        if (auth == null) {
            return Result.unauthorized("请先登录");
        }
        Long userId = (Long) auth.getCredentials();
        CustomerAddress existing = addressService.getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            return Result.error(403, "无权删除此地址");
        }
        addressService.deleteAddress(id);
        return Result.success("删除成功");
    }

    @PutMapping("/default/{id}")
    public Result<?> setDefaultAddress(@PathVariable Long id, Authentication auth) {
        if (auth == null) {
            return Result.unauthorized("请先登录");
        }
        Long userId = (Long) auth.getCredentials();
        addressService.setDefaultAddress(id, userId);
        return Result.success("设置默认地址成功");
    }
}