package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import org.springframework.data.domain.Page;

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
}
