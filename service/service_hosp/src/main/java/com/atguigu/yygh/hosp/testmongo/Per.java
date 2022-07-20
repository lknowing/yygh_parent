package com.atguigu.yygh.hosp.testmongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/20 19:29
 * @FileName: Per
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("Per")
public class Per {
    @Id
    private String id;
    private String name;
    private Integer age;
    private Boolean sex;
    private Integer score;
}
