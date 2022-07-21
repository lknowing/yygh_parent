package com.atguigu.yygh.hosp.testmongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/20 21:22
 * @FileName: PerRepository
 */
@Repository
public interface PerRepository extends MongoRepository<Per,String> {
}
