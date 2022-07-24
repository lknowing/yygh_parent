package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

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

    //根据医院编号 和 科室编号 ，查询排班规则数据
    @Override
    public Map<String, Object> getScheduleRule(long page, long limit,
                                               String hoscode, String depcode) {
        //1.创建返回对象
        Map<String, Object> result = new HashMap<>();
        //2.实现带条件带分页的聚合查询(List)
        //2.1创建查询条件的对象
        Criteria criteria = Criteria
                .where("hoscode").is(hoscode)
                .and("depcode").is(depcode);
        //2.2创建聚合查询的对象
        Aggregation agg = Aggregation.newAggregation(
                //2.2.1设置查询条件
                Aggregation.match(criteria),
                //2.2.2设置聚合参数 + 聚合查询的字段
                Aggregation.group("workDate")
                        .first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                //2.2.3排序
                Aggregation.sort(Sort.Direction.ASC, "workDate"),
                //2.2.4分页
                Aggregation.skip((page - 1) * limit),
                Aggregation.limit(limit)
        );
        //2.3进行聚合查询
        AggregationResults<BookingScheduleRuleVo> aggregate =
                mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList =
                aggregate.getMappedResults();
        //3.实现带条件的聚合查询(total)
        //3.1创建聚合查询对象
        Aggregation aggTotal = Aggregation.newAggregation(
                //3.2.1设置查询条件
                Aggregation.match(criteria),
                //3.2.2设置聚合参数
                Aggregation.group("workDate")
        );
        //3.2进行聚合查询
        AggregationResults<BookingScheduleRuleVo> aggregateTotal =
                mongoTemplate.aggregate(aggTotal, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> totalList =
                aggregateTotal.getMappedResults();
        //3.3获取total
        int total = totalList.size();
        //4.遍历数据换算周几
        for (BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVoList) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }
        //获取医院名称
        String hosName = hospitalService.getHospName(hoscode);
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname", hosName);
        //5.封装数据返回
        result.put("bookingScheduleRuleList", bookingScheduleRuleVoList);
        result.put("total", total);
        result.put("baseMap", baseMap);

        return result;
    }

    /**
     * 根据医院编号 、科室编号和工作日期，查询排班详细信息
     *
     * @param hoscode
     * @param depcode
     * @param workDate
     * @return
     */
    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
        //1.查询派班室数据
        List<Schedule> list =
                scheduleRepository.getByHoscodeAndDepcodeAndWorkDate(
                        hoscode, depcode, new DateTime(workDate).toDate()
                );
        //2.翻译字段
        list.stream().forEach(item -> {
            this.packageSchedule(item);
        });
        return list;
    }

    //封装排班详情其他值 医院名称、科室名称、日期对应星期
    private void packageSchedule(Schedule schedule) {
        //设置医院名称
        schedule.getParam().put("hosname", hospitalService.getHospName(schedule.getHoscode()));
        //设置科室名称
        schedule.getParam().put("depname",
                departmentService.getDepName(schedule.getHoscode(), schedule.getDepcode()));
        //设置日期对应星期
        schedule.getParam().put("dayOfWeek", this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }

    /**
     * 根据日期获取周几数据
     *
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }
}
