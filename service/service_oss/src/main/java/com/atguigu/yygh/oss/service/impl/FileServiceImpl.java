package com.atguigu.yygh.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.oss.service.FileService;
import com.atguigu.yygh.oss.utils.ConstantPropertiesUtil;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/30 09:02
 * @FileName: FileServiceImpl
 */
@Service
public class FileServiceImpl implements FileService {
    /**
     * 文件上传阿里云
     *
     * @param file
     * @return
     */
    @Override
    public String upload(MultipartFile file) {
        //1.获取参数
        String endpoint = ConstantPropertiesUtil.END_POINT;
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = ConstantPropertiesUtil.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtil.ACCESS_KEY_SECRET;
        // 填写Bucket名称，例如examplebucket。
        String bucketName = ConstantPropertiesUtil.BUCKET_NAME;

        //2.创建客户端对象，创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder()
                .build(endpoint, accessKeyId, accessKeySecret);
        try {
            //3.准备文件参数
            //上传文件流
            InputStream inputStream = file.getInputStream();
            //3.1获取文件名
            String filename = file.getOriginalFilename();
            //3.2生成随机唯一值，使用uuid，添加到文件名称里面，放在文件名前面，因为文件名有后缀（.jpg等）
            String uuid = UUID.randomUUID().toString().replace("-", "");
            filename = uuid + filename;
            //3.3根据日期创建路径2022/07/30/ + filename
            String path = new DateTime().toString("yyyy/MM/dd");
            filename = path + "/" + filename;
            //4.上传文件
            ossClient.putObject(bucketName, filename, inputStream);
            //5.获取url
            //https://yygh-0314test.oss-cn-beijing.aliyuncs.com/profile_ss.jpg
            String url = "https://" + bucketName + "." + endpoint + "/" + filename;
            //返回url
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            throw new YyghException(20001, "上传文件失败");
        } finally {
            if (ossClient != null) {
                //6.客户端关闭
                ossClient.shutdown();
            }
        }
    }
}
