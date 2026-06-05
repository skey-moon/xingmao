package com.xingmao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xingmao.entity.Food;
import com.xingmao.mapper.FoodMapper;
import com.xingmao.service.FoodService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class FoodServiceImpl extends ServiceImpl<FoodMapper, Food> implements FoodService {

    @Override
    public Page<Food> getPage(int pageNum, int pageSize, String name, String category, String sortField, String sortOrder) {
        Page<Food> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Food> wrapper = new QueryWrapper<>();

        if (StringUtils.hasText(name)) {
            wrapper.like("name", name);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq("category", category);
        }

        // 排序 - 映射前端字段名到数据库列名
        if (StringUtils.hasText(sortField)) {
            String order = "asc".equalsIgnoreCase(sortOrder) ? "asc" : "desc";
            // 字段名映射
            String column = "name";
            if ("price".equals(sortField)) {
                column = "price";
            } else if ("stock".equals(sortField)) {
                column = "stock";
            } else if ("category".equals(sortField)) {
                column = "category";
            } else if ("createTime".equals(sortField)) {
                column = "create_time";
            }
            wrapper.orderBy(true, "asc".equals(order), column);
        } else {
            wrapper.orderByDesc("create_time");
        }

        return this.page(page, wrapper);
    }

    @Override
    public void addFood(Food food) {
        this.save(food);
    }

    @Override
    public void updateFood(Food food) {
        this.updateById(food);
    }

    @Override
    public void deleteFood(Long id) {
        this.removeById(id);
    }

    @Override
    public List<String> getAllCategories() {
        return this.list()
                .stream()
                .map(Food::getCategory)
                .distinct()
                .filter(c -> c != null && !c.isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }
}