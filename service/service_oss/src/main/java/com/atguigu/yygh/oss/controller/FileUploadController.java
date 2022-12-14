package com.atguigu.yygh.oss.controller;

import com.atguigu.yygh.common.R;
import com.atguigu.yygh.oss.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/30 08:58
 * @FileName: FileUploadController
 */
@Api(tags = "阿里云文件管理")
@RestController
@RequestMapping("/admin/oss/file")
public class FileUploadController {
    @Autowired
    private FileService fileService;

    @ApiOperation(value = "文件上传")
    @PostMapping("upload")
    public R upload(@RequestParam("file") MultipartFile file) {
        String url = fileService.upload(file);
        return R.ok().message("文件上传成功").data("url", url);
    }
}
