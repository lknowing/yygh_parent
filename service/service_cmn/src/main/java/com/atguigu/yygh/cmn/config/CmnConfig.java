package com.atguigu.yygh.cmn.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/19 09:15
 * @FileName: CmnConfig
 */
@Configuration
@EnableTransactionManagement
@MapperScan("com.atguigu.yygh.cmn.mapper")
public class CmnConfig {
}
