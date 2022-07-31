package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    //带条件带分页查询用户列表
    Page<UserInfo> selectPage(Page<UserInfo> pageParams, UserInfoQueryVo userInfoQueryVo);

    //锁定
    void lock(Long userId, Integer status);

    //用户详情
    Map<String, Object> show(Long userId);

    //认证审批
    void approval(Long userId, Integer authStatus);
}
