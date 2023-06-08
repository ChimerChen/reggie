package com.chimer.reggie.mapper;

import com.chimer.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper_1 {
    int insert(User user);
}
