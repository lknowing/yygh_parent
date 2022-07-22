package com.atguigu.yygh.hosp.service.impl;

import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/13 11:54
 * @FileName: HospitalSetServiceImpl
 */
@Service
public class HospitalSetServiceImpl
        extends ServiceImpl<HospitalSetMapper, HospitalSet>
        implements HospitalSetService {
    /**
     * 获取签名key
     *
     * @param hoscode
     * @return
     */
    @Override
    public String getSignKey(String hoscode) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode", hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(wrapper);
        if (hospitalSet == null) {
            throw new YyghException(20001, "医院信息设置有误");
        }
        return hospitalSet.getSignKey();
    }
}
