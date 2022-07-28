package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.mapper.UserInfoMapper;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.vo.user.LoginVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/27 09:31
 * @FileName: UserInfoServiceImpl
 */
@Service
public class UserInfoServiceImpl extends
        ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 会员登录
     *
     * @param loginVo
     * @return map
     */
    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        //1.取出参数
        String phone = loginVo.getPhone();//手机号
        String code = loginVo.getCode();//验证码
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YyghException(20001, "登录信息有误");
        }
        //2.校验验证码
        //2.1根据手机号从redis取出验证码
        String redisCode = redisTemplate.opsForValue().get(phone);
        //2.2对比验证码
        //输入的验证码已经验空,而且数据库验证码有5分钟时效,可能会失效为空,所以code在前面
        if (!code.equals(redisCode)) {
            throw new YyghException(20001, "验证码有误");
        }
        //3.根据手机号查询用户信息
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        UserInfo userInfo = baseMapper.selectOne(wrapper);
        //4.用户信息为空，走注册步骤
        if (userInfo == null) {
            userInfo = new UserInfo();//用户信息为空,new一个用户信息对象
            userInfo.setPhone(phone);
            userInfo.setStatus(1);
            baseMapper.insert(userInfo);
        }
        //5.不为空判断用户是否被锁定
        if (userInfo.getStatus() == 0) {
            throw new YyghException(20001, "该用户已被锁定");
        }
        //6.补全用户信息
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        //7.用户登录
        //jwt生成token字符串
        String token = JwtHelper.createToken(userInfo.getId(), name);

        map.put("token", token);
        map.put("name", name);
        return map;
    }
}
