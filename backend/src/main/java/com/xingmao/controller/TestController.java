package com.xingmao.controller;

import com.xingmao.utils.Result;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @PostMapping("/echo")
    public Result<?> echo(@RequestBody Map<String, Object> data) {
        return Result.success("收到数据", data);
    }
}