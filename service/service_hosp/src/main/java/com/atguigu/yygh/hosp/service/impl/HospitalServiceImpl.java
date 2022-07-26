package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.BookingRule;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    //注入远程调用数据字典
    @Autowired
    private DictFeignClient dictFeignClient;

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

    @Override
    public Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        //0为第一页
        Pageable pageable = PageRequest.of(page - 1, limit, sort);
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写
        //创建实例
        Example<Hospital> example = Example.of(hospital, matcher);
        Page<Hospital> pageModel = hospitalRepository.findAll(example, pageable);
        //跨模块翻译字段cmn
        pageModel.getContent().stream().forEach(item -> {
            this.packHospital(item);
        });
        return pageModel;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        if (status.intValue() == 0 || status.intValue() == 1) {
            //先查询
            Hospital hospital = hospitalRepository.findById(id).get();
            hospital.setStatus(status);
            hospital.setUpdateTime(new Date());
            //再更新
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Map<String, Object> getHospitalById(String id) {
        //1.根据id查询医院信息,翻译字段
        Hospital hospital = this.packHospital(hospitalRepository.findById(id).get());
        //2.判断后取出医院里的预约规则，取出只是复制，要删除旧的
        BookingRule bookingRule = hospital.getBookingRule();
        hospital.setBookingRule(null);
        //3.封装数据返回
        Map<String, Object> map = new HashMap<>();
        map.put("hospital", hospital);
        map.put("bookingRule", bookingRule);
        return map;
    }

    //医院编号获取医院名称
    @Override
    public String getHospName(String hoscode) {
        Hospital hospital = hospitalRepository.getByHoscode(hoscode);
        if (hospital == null) {
            throw new YyghException(20001, "医院信息有误");
        }
        return hospital.getHosname();
    }

    @Override
    public List<Hospital> findByHosname(String hosname) {
        List<Hospital> list = hospitalRepository.findByHosnameLike(hosname);
        return list;
    }

    //医院预约挂号详情,医院信息
    @Override
    public Map<String, Object> getHospByHoscode(String hoscode) {
        //1.根据hoscode查询医院信息,翻译字段
        Hospital hospital = this.packHospital(hospitalRepository.getByHoscode(hoscode));
        //2.判断后取出医院里的预约规则，取出只是复制，要删除旧的
        BookingRule bookingRule = hospital.getBookingRule();
        hospital.setBookingRule(null);
        //3.封装数据返回
        Map<String, Object> map = new HashMap<>();
        map.put("hospital", hospital);
        map.put("bookingRule", bookingRule);
        return map;
    }

    //医院信息字段翻译
    private Hospital packHospital(Hospital hospital) {
        String hostypeString = dictFeignClient.getName(DictEnum.HOSTYPE.getDictCode(), hospital.getHostype());
        String provinceString = dictFeignClient.getName(hospital.getProvinceCode());

        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());

        hospital.getParam().put("hostypeString", hostypeString);
        hospital.getParam().put("fullAddress", provinceString + cityString + districtString + hospital.getAddress());
        return hospital;
    }
}
