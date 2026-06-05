package com.xingmao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingmao.entity.Food;
import com.xingmao.service.FoodService;
import com.xingmao.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/food")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @GetMapping("/list")
    public Result<?> getList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        Page<Food> page = foodService.getPage(pageNum, pageSize, name, category, sortField, sortOrder);
        return Result.success(page);
    }

    @GetMapping("/categories")
    public Result<?> getCategories() {
        List<String> categories = foodService.getAllCategories();
        return Result.success(categories);
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        Food food = foodService.getById(id);
        return Result.success(food);
    }

    @PostMapping
    public Result<?> addFood(@RequestBody Food food) {
        foodService.addFood(food);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<?> updateFood(@RequestBody Food food) {
        foodService.updateFood(food);
        return Result.success("更新成功");
    }

    @PostMapping("/upload")
    public Result<?> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;
        try {
            File uploadDir = new File("./uploads/");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            file.transferTo(new File("./uploads/" + newFilename));
            Map<String, Object> result = new HashMap<>();
            result.put("url", "/uploads/" + newFilename);
            return Result.success(result);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteFood(@PathVariable Long id) {
        foodService.deleteFood(id);
        return Result.success("删除成功");
    }
}