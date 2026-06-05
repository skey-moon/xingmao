package com.xingmao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xingmao.entity.Food;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FoodMapper extends BaseMapper<Food> {
}