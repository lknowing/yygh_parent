package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

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
}
