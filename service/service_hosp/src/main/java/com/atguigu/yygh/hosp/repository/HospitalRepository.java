package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/22 09:36
 * @FileName: HospitalRepository
 */
@Repository
public interface HospitalRepository extends MongoRepository<Hospital, String> {
    //根据hoscode查询医院信息
    Hospital getByHoscode(String hoscode);
}
