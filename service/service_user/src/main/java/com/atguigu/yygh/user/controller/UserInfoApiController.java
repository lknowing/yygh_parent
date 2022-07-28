package com.atguigu.yygh.user.controller;

import com.atguigu.yygh.common.R;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.utils.IpUtils;
import com.atguigu.yygh.vo.user.LoginVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
