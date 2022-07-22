package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/22 16:16
 * @FileName: ScheduleServiceImpl
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public void saveSchedule(Map<String, Object> paramMap) {
        //1.转换参数类型 paramMap 转换Schedule对象
        String paramMapString = JSONObject.toJSONString(paramMap);
        Schedule schedule = JSONObject.parseObject(paramMapString, Schedule.class);
        //2.查询排班信息（hoscode，hosScheduleId）
        Schedule targetSchedule =
                scheduleRepository.getByHoscodeAndHosScheduleId(
                        schedule.getHoscode(), schedule.getHosScheduleId()
                );
        if (targetSchedule != null) {
            //3.存在，更新
            schedule.setId(targetSchedule.getId());
            schedule.setCreateTime(targetSchedule.getCreateTime());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        } else {
            //4.不存在，新增
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        }
    }
}
