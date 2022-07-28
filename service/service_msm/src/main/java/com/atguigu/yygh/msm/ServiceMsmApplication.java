package com.atguigu.yygh.msm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/27 13:39
 * @FileName: ServiceMsmApplication
 */
//取消数据源自动配置,不用mysql数据库 exclude = DataSourceAutoConfiguration.class
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan(basePackages = "com.atguigu")
public class ServiceMsmApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceMsmApplication.class, args);
    }
}
