package com.atguigu.yygh.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.R;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.utils.ConstantPropertiesUtil;
import com.atguigu.yygh.user.utils.HttpClientUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/29 10:29
 * @FileName: WxApiController
 */
@Controller
@RequestMapping("/api/ucenter/wx")
public class WxApiController {
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取微信登录参数
     */
    @GetMapping("getLoginParam")
    @ResponseBody
    public R genQrConnect(HttpSession session) throws UnsupportedEncodingException {
        Map<String, Object> map = new HashMap<>();
        String redirectUrl = URLEncoder
                .encode(ConstantPropertiesUtil.WX_OPEN_REDIRECT_URL, "UTF-8");
        map.put("appid", ConstantPropertiesUtil.WX_OPEN_APP_ID);
        map.put("redirectUri", redirectUrl);
        map.put("scope", "snsapi_login");
        map.put("state", System.currentTimeMillis() + "");//System.currentTimeMillis()+""
        return R.ok().data(map);
    }

    @GetMapping("callback")
    public String callback(String code, String state, HttpSession session) {
        //1.获取微信回调验证码参数
        System.out.println("code = " + code);
        System.out.println("state = " + state);
        //2.使用临时code验证码访问微信接口，换取access_token、open_id
        //2.1拼写请求url
        //方式一
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        //方式二
        // %s 占位符
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                ConstantPropertiesUtil.WX_OPEN_APP_SECRET,
                code);
        try {
            //2.2借助httpclient工具发送请求，获得响应
            String accessTokenString = HttpClientUtils.get(accessTokenUrl);
            System.out.println("accessTokenString = " + accessTokenString);
            //2.3从JSON串中获取access_token、open_id参数
            JSONObject accessTokenJson = JSONObject.parseObject(accessTokenString);
            String accessToken = accessTokenJson.getString("access_token");
            String openid = accessTokenJson.getString("openid");
            //3.根据open_id查询用户信息
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("openid", openid);
            UserInfo userInfo = userInfoService.getOne(wrapper);
            //4.信息为空，走注册流程
            if (userInfo == null) {
                //5.根据access_token、openid获取用户信息，完成注册流程
                //5.1拼写请求url
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);
                //5.2借助httpclient工具发送请求，获得响应
                String resultInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println("resultInfo:" + resultInfo);
                //5.3转换JSON串，获取返回值
                JSONObject resultInfoJson = JSONObject.parseObject(resultInfo);
                //解析用户信息
                //用户昵称
                String nickname = resultInfoJson.getString("nickname");
                //用户头像
                String headimgurl = resultInfoJson.getString("headimgurl");
                //5.4userInfo中存入信息，完成注册操作
                userInfo = new UserInfo();
                userInfo.setOpenid(openid);
                userInfo.setNickName(nickname);
                userInfo.setStatus(1);
                userInfoService.save(userInfo);
            }
            //6.验证用户是否被锁定
            if (userInfo.getStatus() == 0) {
                throw new YyghException(20001, "该用户已被锁定");
            }
            //7.验证用户是否绑定手机号
            //如果已经绑定手机号，open_id=""
            //如果没有绑定手机号，open_id=微信唯一编号
            HashMap<String, Object> map = new HashMap<>();
            if (userInfo.getPhone() == null) {
                map.put("openid", userInfo.getOpenid());
            } else {
                map.put("openid", "");
            }
            //8.补全用户信息、进行登录
            String name = userInfo.getName();
            if (StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if (StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }
            //用户登录
            //jwt生成token字符串
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token", token);
            map.put("name", name);
            //9.重定向回相关页面
            //跳转到前端页面
            return "redirect:http://localhost:3000/weixin/callback?" +
                    "token=" + map.get("token") + "&openid=" + map.get("openid")
                    + "&name=" + URLEncoder.encode((String) map.get("name"), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            throw new YyghException(20001, "微信扫码登录失败");
        }
    }
}
