package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/22 16:15
 * @FileName: ScheduleRepository
 */
@Repository
public interface ScheduleRepository extends MongoRepository<Schedule, String> {
    Schedule getByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    List<Schedule> getByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date workDate);
}
