package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/27 09:30
 * @FileName: UserInfoService
 */
public interface UserInfoService extends IService<UserInfo> {
    //会员登录
    Map<String, Object> login(LoginVo loginVo);

    //用户认证接口
    void userAuth(Long userId, UserAuthVo userAuthVo);

    //根据用户id获取用户信息
    UserInfo getUserInfo(Long userId);
}
