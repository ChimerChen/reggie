package com.chimer.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chimer.reggie.entity.User;

public interface UserService extends IService<User> {
    int insert(User user);
}
