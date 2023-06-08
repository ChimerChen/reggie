package com.chimer.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chimer.reggie.common.BaseContext;
import com.chimer.reggie.common.CustomException;
import com.chimer.reggie.entity.*;
import com.chimer.reggie.mapper.OrderMapper;
import com.chimer.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    @Transactional
    public void submit(Orders orders) {
        //获得用户id
        Long currentId = BaseContext.getCurrentId();
        //查询购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        if (list == null || list.size() == 0){
            throw new CustomException("购物车为空，不能下单");
        }

        User user = userService.getById(currentId);

        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);

        if (addressBook == null){
            throw new CustomException("地址信息有误，不能下单");
        }

        long id = IdWorker.getId();
        //向order插入数据
        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetailList = list.stream().map((item) -> {
            //向order_detail插入数据
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(item,orderDetail,"id");
            orderDetail.setOrderId(id);
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setNumber(String.valueOf(id));
        orders.setId(id);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(currentId);
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                +(addressBook.getCityName() == null ? "" :addressBook.getCityName())
                +(addressBook.getDistrictName() == null ? "" :addressBook.getDistrictName())
                +(addressBook.getDetail() == null ? "" :addressBook.getDetail())
        );
        this.save(orders);


        orderDetailService.saveBatch(orderDetailList);


        //下单完成清空购物车
        shoppingCartService.remove(queryWrapper);
    }
}
