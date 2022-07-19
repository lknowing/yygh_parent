package com.atguigu.yygh.cmn.test;

import com.alibaba.excel.EasyExcel;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/19 11:42
 * @FileName: ReadTest
 */
public class ReadTest {
    public static void main(String[] args) {
        String fileName = "F:\\11.xlsx";
        EasyExcel.read(fileName, Stu.class, new ExcelListener()).sheet().doRead();
    }
}
