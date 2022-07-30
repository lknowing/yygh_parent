package com.atguigu.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/30 09:01
 * @FileName: FileService
 */
public interface FileService {
    /**
     * 文件上传至阿里云
     *
     * @param file
     * @return
     */
    String upload(MultipartFile file);
}
