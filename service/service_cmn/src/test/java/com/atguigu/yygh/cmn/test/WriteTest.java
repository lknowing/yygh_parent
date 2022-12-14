package com.atguigu.yygh.cmn.test;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/19 11:26
 * @FileName: WriteTest
 */
public class WriteTest {
    public static void main(String[] args) {
        // 写法1
        String fileName = "F:\\11.xlsx";
        EasyExcel.write(fileName, Stu.class)
                .sheet("学生信息")
                .doWrite(data());

    }

    //循环设置要添加的数据，最终封装到list集合中
    private static List<Stu> data() {
        List<Stu> list = new ArrayList<Stu>();
        for (int i = 0; i < 10; i++) {
            Stu data = new Stu();
            data.setSno(i);
            data.setSname("张三" + i);
            list.add(data);
        }
        return list;
    }
}
