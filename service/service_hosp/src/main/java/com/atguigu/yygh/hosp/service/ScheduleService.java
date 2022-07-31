package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/22 16:16
 * @FileName: ScheduleService
 */
public interface ScheduleService {
    void saveSchedule(Map<String, Object> paramMap);

    Page selectPage(int page, int limit, ScheduleQueryVo scheduleQueryVo);

    void removeSchedule(String hoscode, String hosScheduleId);

    //根据医院编号 和 科室编号 ，查询排班规则数据
    Map<String, Object> getScheduleRule(long page, long limit, String hoscode, String depcode);

    //根据医院编号 、科室编号和工作日期，查询排班详细信息
    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    //获取可预约排班数据
    Map<String, Object> getBookingSchedule(Integer page, Integer limit, String hoscode, String depcode);
}
