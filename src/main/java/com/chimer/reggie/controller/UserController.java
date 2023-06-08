package com.chimer.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chimer.reggie.common.R;
import com.chimer.reggie.entity.User;
import com.chimer.reggie.service.UserService;
import com.chimer.reggie.utils.SMSUtils;
import com.chimer.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequestMapping("/user")
@Slf4j
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;


    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();

        //生成验证码
        if (StringUtils.hasText(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            log.info("code={}",code);
//            SMSUtils.sendMessage("瑞吉外卖","",phone,code);
//            session.setAttribute(phone,code);
            return  R.success("短信发送成功");
        }

        //需要将生成的验证码保存在session

        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        //获取手机号和验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //从session中获取保存的验证码
//        Object codeInSession = session.getAttribute(phone);
        Object o = redisTemplate.opsForValue().get(phone);

        if (o != null && o.equals(code)){
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            redisTemplate.delete(phone);
            return R.success(user);
        }
        //比对
        //判断是否为新用户
        return R.error("登陆失败");
    }

    @PostMapping
    public R<String> addUser(@RequestBody User user){
        System.out.println(user.toString());
        int N = userService.insert(user);
        System.out.println(N);

        return R.success("插入成功");
    }
}
