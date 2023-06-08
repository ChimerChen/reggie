package com.chimer.reggie.dto;

import com.chimer.reggie.entity.Setmeal;
import com.chimer.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
