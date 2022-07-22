package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/22 09:39
 * @FileName: HospitalServiceImpl
 */
@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;

    //上传医院
    @Override
    public void saveHospital(Map<String, Object> paramMap) {
        //1.转化参数类型paramMap=>Hospital
        String paramJsonString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(paramJsonString, Hospital.class);
        //2.根据hoscode查询医院信息
        Hospital targetHospital = hospitalRepository.getByHoscode(hospital.getHoscode());
        //3.判断有医院信息进行更新
        if (targetHospital != null) {
            hospital.setId(targetHospital.getId());
            hospital.setCreateTime(targetHospital.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setStatus(targetHospital.getStatus());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        } else {
            //4.没有医院信息进行新增
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setStatus(0);
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        return hospitalRepository.getByHoscode(hoscode);
    }
}
