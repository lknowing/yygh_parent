package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
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

    Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    //更新上线状态
    void updateStatus(String id, Integer status);

    Map<String, Object> getHospitalById(String id);

    String getHospName(String hoscode);

    //根据医院名称获取医院列表
    List<Hospital> findByHosname(String hosname);

    Map<String, Object> getHospByHoscode(String hoscode);
}
