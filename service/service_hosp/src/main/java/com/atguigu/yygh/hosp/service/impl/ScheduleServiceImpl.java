package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.BookingRule;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * 获取可预约排班数据
     *
     * @param page
     * @param limit
     * @param hoscode
     * @param depcode
     * @return map
     */
    @Override
    public Map<String, Object> getBookingSchedule(Integer page, Integer limit, String hoscode, String depcode) {
        Map<String, Object> result = new HashMap<>();

        //1.根据hoscode查询医院信息，获取预约规则
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        if (hospital == null) {
            throw new YyghException(20001, "医院信息有误");
        }
        BookingRule bookingRule = hospital.getBookingRule();
        //2.根据预约规则、分页信息查询可预约日期集合分页对象(IPage<Date>)
        IPage<Date> iPage = this.getDateListPage(page, limit, bookingRule);
        List<Date> datePageList = iPage.getRecords();
        //3.参考后台接口进行聚合查询(List<BookingScheduleRuleVo>)
        //3.1创建查询条件的对象
        Criteria criteria = Criteria
                .where("hoscode").is(hoscode)
                .and("depcode").is(depcode)
                .and("workDate").in(datePageList);
        //3.2创建聚合查询的对象
        Aggregation agg = Aggregation.newAggregation(
                //3.2.1设置查询条件
                Aggregation.match(criteria),
                //3.2.2设置聚合参数 + 聚合查询的字段
                Aggregation.group("workDate")
                        .first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber")
        );
        //3.3进行聚合查询 List<BookingScheduleRuleVo>
        AggregationResults<BookingScheduleRuleVo> aggregate =
                mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> scheduleVoList =
                aggregate.getMappedResults();
        //3.4转化查询结果类型，List=>Map k:workDate v:BookingScheduleRuleVo
        Map<Date, BookingScheduleRuleVo> scheduleVoMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(scheduleVoList)) {
            scheduleVoMap = scheduleVoList.stream()
                    .collect(Collectors.toMap(
                            BookingScheduleRuleVo::getWorkDate,
                            BookingScheduleRuleVo -> BookingScheduleRuleVo
                    ));
        }
        //4.合并步骤2(datePageList)和步骤3(scheduleVoMap)的数据
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = new ArrayList<>();
        for (int i = 0, let = datePageList.size(); i < let; i++) {
            //4.1遍历datePageList，取出每一天日期
            Date date = datePageList.get(i);
            //4.2根据日期查询scheduleVoMap，获取排班聚合的记录信息
            BookingScheduleRuleVo bookingScheduleRuleVo = scheduleVoMap.get(date);
            //4.3排班聚合的记录为空，需要初始化
            if (bookingScheduleRuleVo == null) {
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                bookingScheduleRuleVo.setDocCount(0);
                bookingScheduleRuleVo.setAvailableNumber(-1);
            }
            //4.4设置排班日期
            bookingScheduleRuleVo.setWorkDate(date);
            bookingScheduleRuleVo.setWorkDateMd(date);
            //4.5根据日期换算周几
            String dayOfWeek = this.getDayOfWeek(new DateTime(date));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
            //4.6根据时间进行记录的状态判断
            //状态 0：正常 1：即将放号 -1：当天已停止挂号
            //最后一页，最后一条记录，状态为 1:即将放号
            if (i == let - 1 && page == iPage.getPages()) {
                bookingScheduleRuleVo.setStatus(1);
            } else {
                bookingScheduleRuleVo.setStatus(0);
            }
            //第一页，第一条记录，过了已经过了停止挂号时间，状态为 -1：当天已停止挂号
            if (i == 0 && page == 1) {
                DateTime stopDateTime =
                        this.getDateTime(new Date(), bookingRule.getStopTime());
                if (stopDateTime.isBeforeNow()) {
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }
            bookingScheduleRuleVoList.add(bookingScheduleRuleVo);
        }
        //5.封装数据
        //可预约日期规则数据
        result.put("bookingScheduleList", bookingScheduleRuleVoList);
        result.put("total", iPage.getTotal());
        //其他基础的数据
        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospitalService.getHospName(hoscode));
        //科室
        Department department = departmentService.getDepartment(hoscode, depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        result.put("baseMap", baseMap);
        return result;
    }

    //根据预约规则、分页信息查询可预约日期集合分页对象(IPage<Date>)
    private IPage<Date> getDateListPage(
            Integer page, Integer limit, BookingRule bookingRule) {
        //1.从预约规则获取开始预约挂号的时间(当前系统日期+开始时间)
        DateTime releaseDateTime = this.getDateTime(
                new Date(), bookingRule.getReleaseTime()
        );
        //2.从预约规则获取周期，判断周期是否需要 +1
        Integer cycle = bookingRule.getCycle();
        if (releaseDateTime.isBeforeNow()) cycle += 1;
        //3.根据周期推算出可以挂号的日期，存入集合(List)
        List<Date> dateList = new ArrayList<>();
        for (int i = 0; i < cycle; i++) {
            DateTime plusDays = new DateTime().plusDays(i);
            String plusDaysString = plusDays.toString("yyyy-MM-dd");
            dateList.add(new DateTime(plusDaysString).toDate());
        }
        //4.准备分页参数
        int start = (page - 1) * limit;
        int end = (page - 1) * limit + limit;
        if (end > dateList.size()) end = dateList.size();
        //5.根据参数获取分页后日期集合，循环遍历
        List<Date> datePageList = new ArrayList<>();
        for (int i = start; i < end; i++) {
            datePageList.add(dateList.get(i));
        }
        //6.封装数据到IPage对象返回
        IPage<Date> iPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, limit, dateList.size());
        iPage.setRecords(datePageList);
        return iPage;
    }

    //日期+开始时间
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date)
                .toString("yyyy-MM-dd") + " " + timeString;
        DateTime dateTime = DateTimeFormat
                .forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
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
