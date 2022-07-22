package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;

import java.util.Map;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/22 09:38
 * @FileName: HospitalService
 */
public interface HospitalService {
    //上传医院
    void saveHospital(Map<String, Object> paramMap);

    Hospital getByHoscode(String hoscode);
}
