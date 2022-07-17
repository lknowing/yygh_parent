package com.atguigu.yygh.hosp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/17 16:41
 * @FileName: ServiceCmnApplication
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.atguigu")
public class ServiceCmnApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCmnApplication.class, args);
    }
}
