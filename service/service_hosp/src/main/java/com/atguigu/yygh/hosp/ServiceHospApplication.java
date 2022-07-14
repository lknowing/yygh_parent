package com.atguigu.yygh.hosp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/13 11:45
 * @FileName: ServiceHospApplication
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.atguigu")
//@MapperScan("com.atguigu.yygh.hosp.mapper")
public class ServiceHospApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplication.class, args);
    }
}
