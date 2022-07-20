package com.atguigu.yygh.hosp.testmongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/20 15:30
 * @FileName: UserRepository
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    List getByNameAndAge(String name, int age);

    List getByNameLike(String name);
}
