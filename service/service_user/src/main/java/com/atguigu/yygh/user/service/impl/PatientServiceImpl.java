package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.mapper.PatientMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/30 14:29
 * @FileName: PatientServiceImpl
 */
@Service
public class PatientServiceImpl extends
        ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    private DictFeignClient dictFeignClient;

    /**
     * 获取就诊人列表
     *
     * @param userId
     * @return List<Patient>
     */
    @Override
    public List<Patient> findAll(Long userId) {
        //1.查询就诊人信息
        QueryWrapper<Patient> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<Patient> patientList = baseMapper.selectList(wrapper);
        //2.遍历集合翻译字段
        patientList.stream().forEach(item -> {
            this.packPatient(item);
        });
        return patientList;
    }

    /**
     * 根据id获取就诊人信息
     *
     * @param id
     * @return patient
     */
    @Override
    public Patient getPatient(Long id) {
        Patient patient = this.packPatient(baseMapper.selectById(id));
        return patient;
    }

    /**
     * Patient对象里面其他参数封装
     *
     * @param patient
     * @return patient
     */
    private Patient packPatient(Patient patient) {
        //根据证件类型编码，获取证件类型具体指
        String certificatesTypeString =
                dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getCertificatesType());//联系人证件
        //联系人证件类型
        String contactsCertificatesTypeString =
                dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getContactsCertificatesType());
        //省
        String provinceString = dictFeignClient.getName(patient.getProvinceCode());
        //市
        String cityString = dictFeignClient.getName(patient.getCityCode());
        //区
        String districtString = dictFeignClient.getName(patient.getDistrictCode());
        patient.getParam().put("certificatesTypeString", certificatesTypeString);
        patient.getParam().put("contactsCertificatesTypeString", contactsCertificatesTypeString);
        patient.getParam().put("provinceString", provinceString);
        patient.getParam().put("cityString", cityString);
        patient.getParam().put("districtString", districtString);
        patient.getParam().put("fullAddress", provinceString + cityString + districtString + patient.getAddress());
        return patient;
    }
}
