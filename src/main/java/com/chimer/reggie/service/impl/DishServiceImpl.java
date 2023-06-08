package com.chimer.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chimer.reggie.dto.DishDto;
import com.chimer.reggie.entity.Dish;
import com.chimer.reggie.entity.DishFlavor;
import com.chimer.reggie.mapper.DishMapper;
import com.chimer.reggie.service.DishFlavorService;
import com.chimer.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品信息到菜品表
        this.save(dishDto);

        Long dishId = dishDto.getId();

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item -> {
            item.setDishId(dishId);
            return item;
        })).collect(Collectors.toList());
        //保存菜品口味到菜品口味表
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    /**
     * 根据id查询菜品信息和口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //查询当前才菜品的对应的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);


        dishDto.setFlavors(flavors);


        return dishDto;
    }

    @Override
    @Transactional  //事务注解
    public void updateWithFlavor(DishDto dishDto) {
        //更新基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //添加当前为提交的口味数据

        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item -> {
            item.setDishId(dishDto.getId());
            return item;
        })).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);


    }
}
