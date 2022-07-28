package com.atguigu.yygh.msm.service;

import java.util.Map;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/27 13:43
 * @FileName: MsmService
 */
public interface MsmService {
    //发送短信
    boolean send(String phone, Map<String, String> paramMap);
}
