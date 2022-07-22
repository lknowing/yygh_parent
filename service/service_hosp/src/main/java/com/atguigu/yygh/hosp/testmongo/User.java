package com.atguigu.yygh.hosp.testmongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/20 14:33
 * @FileName: User
 */
@Data
@Document("User")
public class User {
    @Id
    private String id;
    private String name;
    private Integer age;
    private String email;
    private String createDate;
}