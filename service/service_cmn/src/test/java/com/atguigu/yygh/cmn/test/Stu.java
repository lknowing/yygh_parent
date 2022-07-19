package com.atguigu.yygh.cmn.test;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/19 11:25
 * @FileName: Stu
 */
@Data
public class Stu {

    //设置表头名称
    //设置列对应的属性
    @ExcelProperty(value = "学生编号", index = 0)
    private int sno;

    //设置表头名称
    //设置列对应的属性
    @ExcelProperty(value = "学生姓名", index = 1)
    private String sname;

}
