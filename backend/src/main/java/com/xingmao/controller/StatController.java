package com.xingmao.controller;

import com.xingmao.service.StatService;
import com.xingmao.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stat")
public class StatController {

    @Autowired
    private StatService statService;

    @GetMapping("/dashboard")
    public Result<?> getDashboardStats() {
        Map<String, Object> stats = statService.getDashboardStats();
        return Result.success(stats);
    }
}