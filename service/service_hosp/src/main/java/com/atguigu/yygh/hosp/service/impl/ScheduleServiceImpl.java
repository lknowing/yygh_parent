package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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

    @Override
    public Page selectPage(int page, int limit, ScheduleQueryVo scheduleQueryVo) {
        //1.创建分页查询对象
        //1.1创建排序对象
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        //1.2创建分页对象（第一页为0）
        Pageable pageable = PageRequest.of(page - 1, limit, sort);
        //2.创建条件查询模板
        //2.1设置筛选条件
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        //2.2设置模板构造器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        //2.3创建条件查询模板
        Example<Schedule> example = Example.of(schedule, matcher);
        //3.进行条件分页查询
        Page<Schedule> pageModel = scheduleRepository.findAll(example, pageable);
        return pageModel;
    }

    @Override
    public void removeSchedule(String hoscode, String hosScheduleId) {
        Schedule schedule = scheduleRepository.getByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (null == schedule) {
            throw new YyghException(20001, "排班编码有误");
        }
        scheduleRepository.deleteById(schedule.getId());
    }
}
