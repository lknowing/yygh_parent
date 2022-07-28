package com.atguigu.yygh.msm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.msm.service.MsmService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/27 13:46
 * @FileName: MsmServiceImpl
 */
@Service
public class MsmServiceImpl implements MsmService {
    //发送短信
    @Override
    public boolean send(String phone, Map<String, String> paramMap) {
        //1.校验手机号验空
        if (StringUtils.isEmpty(phone)) {
            return false;
        }
        //2.创建和阿里云对接的客户端对象
        DefaultProfile profile = DefaultProfile
                .getProfile("default",
                        "LTAI5tGAKxU9GGLpPGZGofw2",
                        "Cn1iPIqRZNfrQQkldRScgRCnlJAybO");

        IAcsClient client = new DefaultAcsClient(profile);
        //3.创建请求对象request，塞入必要的参数
        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");

        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", "我的谷粒在线教育网站");
        request.putQueryParameter("TemplateCode", "SMS_183195440");
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(paramMap));
        try {
            //4.使用客户端对象，调用其方法，发送请求，获取响应response
            CommonResponse response = client.getCommonResponse(request);
            //5.从响应中获取结果
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            throw new YyghException(20001, "短信发送失败");
        }
    }
}
