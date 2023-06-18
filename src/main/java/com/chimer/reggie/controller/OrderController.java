package com.chimer.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chimer.reggie.common.R;
import com.chimer.reggie.dto.OrderDto;
import com.chimer.reggie.entity.Orders;
import com.chimer.reggie.entity.User;
import com.chimer.reggie.service.OrderService;
import com.chimer.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/order")
@Slf4j
@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String number){
        Page<Orders> pageInfo = new Page<>(page,pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.orderByDesc(Orders::getCheckoutTime);
        queryWrapper.like(number != null,Orders::getNumber,number);

        orderService.page(pageInfo,queryWrapper);


        //写入username

        Page<OrderDto> orderDtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo,orderDtoPage,"records");
        List<Orders> records = pageInfo.getRecords();

        List<OrderDto> list = records.stream().map((item -> {
            OrderDto orderDto = new OrderDto();
            BeanUtils.copyProperties(item, orderDto);
            Long userId = item.getUserId();
            User user = userService.getById(userId);
            orderDto.setUserName(user.getName());
            return orderDto;
        })).collect(Collectors.toList());

        orderDtoPage.setRecords(list);

        return R.success(orderDtoPage);

    }

}
