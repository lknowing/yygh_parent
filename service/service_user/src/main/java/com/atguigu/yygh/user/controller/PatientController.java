package com.atguigu.yygh.user.controller;

import com.atguigu.yygh.common.R;
import com.atguigu.yygh.common.utils.AuthContextHolder;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.service.PatientService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * title:就诊人管理接口
 *
 * @Author xu
 * @Date 2022/07/30 14:30
 * @FileName: Patientcontroller
 */
@RestController
@RequestMapping("/api/user/patient")
public class PatientController {
    @Autowired
    private PatientService patientService;

    //获取就诊人列表
    @ApiOperation(value = "获取就诊人列表")
    @GetMapping("auth/findAll")
    public R findAll(HttpServletRequest request) {
        //1.取出用户id
        Long userId = AuthContextHolder.getUserId(request);
        //2.调用接口获取就诊人列表
        List<Patient> list = patientService.findAll(userId);
        return R.ok().data("list", list);
    }

    //添加就诊人
    @PostMapping("auth/save")
    public R savePatient(@RequestBody Patient patient,
                         HttpServletRequest request) {
        //1.取出用户id
        Long userId = AuthContextHolder.getUserId(request);
        //2.添加用户id到就诊人信息里面
        patient.setUserId(userId);
        patientService.save(patient);
        return R.ok();
    }

    //根据id获取就诊人信息
    @GetMapping("auth/get/{id}")
    public R getPatient(@PathVariable Long id) {
        Patient patient = patientService.getPatient(id);
        return R.ok().data("patient", patient);
    }

    //修改就诊人
    @PostMapping("auth/update")
    public R updatePatient(@RequestBody Patient patient) {
        patientService.updateById(patient);
        return R.ok();
    }

    //删除就诊人
    @DeleteMapping("auth/remove/{id}")
    public R removePatient(@PathVariable Long id) {
        patientService.removeById(id);
        return R.ok();
    }
}
