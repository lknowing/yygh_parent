package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.enums.AuthStatusEnum;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.mapper.UserInfoMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private PatientService patientService;

//    @Autowired
//    private UserInfoService userInfoService;

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
        String openid = loginVo.getOpenid();
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YyghException(20001, "登录信息有误");
        }
        //2.校验验证码
        //2.1根据手机号从redis取出验证码
        String redisCode = redisTemplate.opsForValue().get(phone);
        //2.2对比验证码
        //输入的验证码已经验空,而且数据库验证码redisCode有5分钟时效,可能会失效为空,所以code在前面
        if (!code.equals(redisCode)) {
            throw new YyghException(20001, "验证码有误");
        }
        Map<String, Object> map = new HashMap<>();
        //3.判断openid为空走普通的手机验证码登录，不为空走绑定手机号
        if (StringUtils.isEmpty(openid)) {
            //3.1.1根据手机号查询用户信息
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("phone", phone);
            UserInfo userInfo = baseMapper.selectOne(wrapper);
            //3.1.2用户信息为空，走注册步骤
            if (userInfo == null) {
                userInfo = new UserInfo();//用户信息为空,new一个用户信息对象
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
            //3.1.3判断锁定，补全，登录
            get(map, userInfo);
        } else {
            //3.2.1根据openid查询用户信息
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("openid", openid);
            UserInfo userInfo = baseMapper.selectOne(wrapper);
            if (userInfo == null) {
                throw new YyghException(20001, "用户注册信息有误");
            }
            //3.2.2更新用户手机号信息
            userInfo.setPhone(phone);
            baseMapper.updateById(userInfo);
            //userInfoService.updateById(userInfo);
            //3.2.3判断锁定，补全，登录
            get(map, userInfo);
        }
        return map;
    }

    /**
     * 判断锁定，补全，登录
     *
     * @param map
     * @param userInfo
     */
    private void get(Map<String, Object> map, UserInfo userInfo) {
        //4.不为空判断用户是否被锁定
        if (userInfo.getStatus() == 0) {
            throw new YyghException(20001, "该用户已被锁定");
        }
        //5.补全用户信息
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        //6.用户登录
        //jwt生成token字符串
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);
        map.put("name", name);
    }

    /**
     * 根据用户id获取用户信息
     *
     * @param userId
     * @return UserInfo
     */
    @Override
    public UserInfo getUserInfo(Long userId) {
        //1.根据用户id查询用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        if (userInfo == null) {
            throw new YyghException(20001, "用户信息有误");
        }
        //2.翻译字段，认证状态（认证中）
        userInfo = this.packUserInfo(userInfo);
        return userInfo;
    }

    /**
     * 带条件带分页查询用户列表
     *
     * @param pageParams
     * @param userInfoQueryVo
     * @return pageModel
     */
    @Override
    public Page<UserInfo> selectPage(Page<UserInfo> pageParams, UserInfoQueryVo userInfoQueryVo) {
        //1.取出查询条件
        //UserInfoQueryVo获取条件值
        String name = userInfoQueryVo.getKeyword(); //用户名称
        Integer status = userInfoQueryVo.getStatus();//用户状态
        Integer authStatus = userInfoQueryVo.getAuthStatus(); //认证状态
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin(); //开始时间
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd(); //结束时间
        //2.验空进行条件拼装
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(name)) {
            wrapper.like("name", name);
        }
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("status", status);
        }
        if (!StringUtils.isEmpty(authStatus)) {
            wrapper.eq("auth_status", authStatus);
        }
        if (!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time", createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time", createTimeEnd);
        }
        //3.分页查询
        Page<UserInfo> pageModel = baseMapper.selectPage(pageParams, wrapper);
        //4.翻译字段(不需要跨模块，直接数据翻译)
        pageModel.getRecords().stream().forEach(item -> {
            this.packUserInfo(item);
        });
        return pageModel;
    }

    /**
     * 锁定
     *
     * @param userId
     * @param status
     */
    @Override
    public void lock(Long userId, Integer status) {
        if (status.intValue() == 0 || status.intValue() == 1) {
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        }
    }

    /**
     * 用户详情
     *
     * @param userId
     * @return
     */
    @Override
    public Map<String, Object> show(Long userId) {
        //1.根据用户id查询用户信息(翻译字段)
        UserInfo userInfo = this.packUserInfo(baseMapper.selectById(userId));
        //2.根据用户id查询就诊人信息
        List<Patient> patientList = patientService.findAll(userId);
        //3.封装map返回
        HashMap<String, Object> map = new HashMap<>();
        map.put("userInfo", userInfo);
        map.put("patientList", patientList);
        return map;
    }

    /**
     * 认证审批  2通过  -1不通过
     *
     * @param userId
     * @param authStatus
     */
    @Override
    public void approval(Long userId, Integer authStatus) {
        if (authStatus.intValue() == 2 || authStatus.intValue() == -1) {
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }
    }

    /**
     * 翻译用户相关字段
     *
     * @param userInfo
     * @return UserInfo
     */
    private UserInfo packUserInfo(UserInfo userInfo) {
        //处理认证状态编码
        String statusNameByStatus = AuthStatusEnum
                .getStatusNameByStatus(userInfo.getAuthStatus());
        userInfo.getParam().put("authStatusString", statusNameByStatus);
        //处理用户状态 0  1
        String statusString = userInfo.getStatus().intValue() == 0 ? "锁定" : "正常";
        userInfo.getParam().put("statusString", statusString);
        return userInfo;
    }

    /**
     * 用户认证接口
     *
     * @param userId
     * @param userAuthVo
     */
    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        //1.根据用户id查询用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        if (userInfo == null) {
            throw new YyghException(20001, "用户信息有误");
        }
        //2.更新认证信息
        BeanUtils.copyProperties(userAuthVo, userInfo);
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        baseMapper.updateById(userInfo);
    }
}
