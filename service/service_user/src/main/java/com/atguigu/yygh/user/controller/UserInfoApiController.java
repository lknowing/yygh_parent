package com.atguigu.yygh.user.controller;

import com.atguigu.yygh.common.R;
import com.atguigu.yygh.common.utils.AuthContextHolder;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.utils.IpUtils;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/27 09:33
 * @FileName: UserInfoApiController
 */
@RestController
@RequestMapping("/api/user")
public class UserInfoApiController {

    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation(value = "会员登录")
    @PostMapping("login")
    public R login(@RequestBody LoginVo loginVo, HttpServletRequest request) {
        loginVo.setIp(IpUtils.getIpAddr(request));
        Map<String, Object> info = userInfoService.login(loginVo);
        return R.ok().data(info);
    }

    //用户认证接口
    @ApiOperation(value = "用户认证提交")
    @PostMapping("auth/userAuth")
    public R userAuth(@RequestBody UserAuthVo userAuthVo,
                      HttpServletRequest request) {
        //1.取出用户id
        Long userId = AuthContextHolder.getUserId(request);
        //2.更新用户认证信息
        userInfoService.userAuth(userId, userAuthVo);
        return R.ok();
    }

    //获取用户信息接口
    @ApiOperation(value = "根据用户id获取认证信息")
    @GetMapping("auth/getUserInfo")
    public R getUserInfo(HttpServletRequest request) {
        //1.取出用户id
        Long userId = AuthContextHolder.getUserId(request);
//        if (userId == null) {
//            return R.ok();
//        }
        //2.根据用户id获取用户认证信息
        UserInfo userInfo = userInfoService.getUserInfo(userId);
        return R.ok().data("userInfo", userInfo);
    }
}
