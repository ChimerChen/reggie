package com.chimer.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chimer.reggie.entity.AddressBook;
import com.chimer.reggie.mapper.AddressBookMapper;
import com.chimer.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;


@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
