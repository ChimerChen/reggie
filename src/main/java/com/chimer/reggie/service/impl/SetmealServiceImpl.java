package com.chimer.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chimer.reggie.common.CustomException;
import com.chimer.reggie.dto.SetmealDto;
import com.chimer.reggie.entity.Dish;
import com.chimer.reggie.entity.Setmeal;
import com.chimer.reggie.entity.SetmealDish;
import com.chimer.reggie.mapper.SetmealMapper;
import com.chimer.reggie.service.DishService;
import com.chimer.reggie.service.SetmealDishService;
import com.chimer.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private DishService dishService;
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //写入setmealId
        setmealDishes.stream().map((item->{
            item.setSetmealId(setmealDto.getId());
            return item;
        })).collect(Collectors.toList());
        //保存套餐和菜品的的关联关系
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐和其关联数据
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.in(Setmeal::getId,ids);

        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);


        //不能删除，抛出业务异常
        if(count > 0){
            throw new CustomException("套餐正在售卖中，请先停售");
        }

        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);


        setmealDishService.remove(queryWrapper1);

    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());

        setmealDishService.remove(queryWrapper);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes.stream().map((item->{
            item.setSetmealId(setmealDto.getId());
            return item;
        })).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }
}
