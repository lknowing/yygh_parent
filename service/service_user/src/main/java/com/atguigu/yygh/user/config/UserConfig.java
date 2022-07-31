package com.atguigu.yygh.user.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/31 10:10
 * @FileName: UserConfig
 */
@Configuration
@EnableTransactionManagement
public class UserConfig {
    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
