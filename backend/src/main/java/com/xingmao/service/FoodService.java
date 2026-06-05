package com.xingmao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xingmao.entity.Food;

import java.util.List;

public interface FoodService extends IService<Food> {

    Page<Food> getPage(int pageNum, int pageSize, String name, String category, String sortField, String sortOrder);

    void addFood(Food food);

    void updateFood(Food food);

    void deleteFood(Long id);

    List<String> getAllCategories();
}