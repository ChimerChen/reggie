package com.chimer.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chimer.reggie.entity.User;
import com.chimer.reggie.mapper.UserMapper;
import com.chimer.reggie.mapper.UserMapper_1;
import com.chimer.reggie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserMapperServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper_1 userMapper1;


    @Override
    public int insert(User user) {
        return userMapper1.insert(user);
    }
}
