package com.chimer.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chimer.reggie.common.CustomException;
import com.chimer.reggie.entity.Category;
import com.chimer.reggie.entity.Dish;
import com.chimer.reggie.entity.Setmeal;
import com.chimer.reggie.mapper.CategoryMapper;
import com.chimer.reggie.service.CategoryService;
import com.chimer.reggie.service.DishService;
import com.chimer.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{
    /**
     * 根据id删除分类
     * 删除之前判断分类是否关联了菜品或套餐
     * @param id
     */
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    @Override
    public void remove(Long id) {

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(dishLambdaQueryWrapper);

        //如果已经关联菜品，抛出业务异常
        if (count > 0){
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        //如果已经关联套餐，抛出业务异常
        if (count1 > 0){
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        super.removeById(id);
    }
}
