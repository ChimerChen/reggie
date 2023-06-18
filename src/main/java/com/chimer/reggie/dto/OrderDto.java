package com.chimer.reggie.dto;

import com.chimer.reggie.entity.Orders;
import lombok.Data;

@Data
public class OrderDto extends Orders {
    private String userName;
}
