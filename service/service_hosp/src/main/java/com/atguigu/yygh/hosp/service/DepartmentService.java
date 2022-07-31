package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/22 14:31
 * @FileName: DepartmentService
 */
public interface DepartmentService {
    /**
     * 上传科室
     *
     * @param paramMap
     */
    void save(Map<String, Object> paramMap);

    /**
     * 带条件带分页查询科室
     *
     * @param page
     * @param limit
     * @param departmentQueryVo
     * @return
     */
    Page selectPage(int page, int limit, DepartmentQueryVo departmentQueryVo);

    /**
     * 删除科室
     *
     * @param hoscode
     * @param depcode
     */
    void removeDepartment(String hoscode, String depcode);

    /**
     * 根据医院编号，查询医院所有科室列表
     *
     * @param hoscode
     * @return
     */
    List<DepartmentVo> findDeptTree(String hoscode);

    /**
     * 根据医院编号，科室编号来查询科室名称
     *
     * @param hoscode
     * @param depcode
     * @return
     */
    String getDepName(String hoscode, String depcode);

    //根据hoscode和depcode查询科室信息
    Department getDepartment(String hoscode, String depcode);
}
