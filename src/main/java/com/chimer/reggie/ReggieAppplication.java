package com.chimer.reggie;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
@MapperScan("com.chimer.reggie.mapper") //映射器类的包路径
@EnableCaching //开启注解缓存
public class ReggieAppplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieAppplication.class,args);
        log.info("项目启动成功.....");
    }
}
