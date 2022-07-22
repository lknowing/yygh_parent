package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/22 14:31
 * @FileName: DepartmentServiceImpl
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void save(Map<String, Object> paramMap) {
        //1.转换参数类型 paramMap 转换department对象
        String paramMapString = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(paramMapString, Department.class);
        //2.查询科室信息 根据医院编号 和 科室编号查询
        Department departmentExist = departmentRepository.
                getByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());
        if (departmentExist != null) {
            //3.判断 存在就更新
            department.setId(departmentExist.getId());
            department.setCreateTime(departmentExist.getCreateTime());
            department.setUpdateTime(new Date());
            department.setIsDeleted(departmentExist.getIsDeleted());
            departmentRepository.save(department);
        } else {
            //4.判断 不存在就新增
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page selectPage(int page, int limit, DepartmentQueryVo departmentQueryVo) {
        //1.创建分页查询对象
        //1.1创建排序对象
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        //1.2创建分页对象(第一页为0)
        Pageable pageable = PageRequest.of(page - 1, limit, sort);
        //2.创建条件查询模板
        //2.1设置筛选条件
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo, department);
        //2.2设置模板构造器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        //2.3创建条件查询模板
        Example<Department> example = Example.of(department, matcher);
        //3.进行带条件带分页查询
        Page<Department> pageModel = departmentRepository.findAll(example, pageable);
        return pageModel;
    }

    @Override
    public void removeDepartment(String hoscode, String depcode) {
        //1.根据hoscode和depcode查询科室信息
        Department department = departmentRepository.getByHoscodeAndDepcode(hoscode, depcode);
        //2.判断科室信息
        if (null == department) {
            throw new YyghException(20001, "科室编码有误");
        }
        //3.根据主键id删除
        departmentRepository.deleteById(department.getId());
    }
}
