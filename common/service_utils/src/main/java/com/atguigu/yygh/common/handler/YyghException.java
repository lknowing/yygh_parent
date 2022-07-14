package com.atguigu.yygh.common.handler;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/13 16:35
 * @FileName: YyghException
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YyghException extends RuntimeException {
    @ApiModelProperty(value = "状态码")
    private Integer code;
    @ApiModelProperty(value = "异常信息")
    private String msg;
}
