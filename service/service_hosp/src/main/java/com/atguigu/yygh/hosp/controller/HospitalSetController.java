package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.R;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * title:医院设置接口
 *
 * @Author xu
 * @Date 2022/07/13 11:56
 * @FileName: HospitalSetController
 */
@Api(description = "医院设置接口")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService;

    // 医院设置锁定和解锁
    @ApiOperation(value = "医院设置锁定和解锁")
    @PutMapping("lockHospitalSet/{id}/{status}")
    public R lockHospitalSet(@PathVariable Long id,
                             @PathVariable Integer status) {
        //1.根据id查询医院设置信息
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        //2.设置状态
        hospitalSet.setStatus(status);
        //3.调用方法
        boolean updateById = hospitalSetService.updateById(hospitalSet);
        if (updateById) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    //批量删除方法
    @ApiOperation(value = "批量医院设置删除")
    @DeleteMapping("batchRemove")
    public R batchRemove(@RequestBody List<Long> idList) {
        boolean batchRemove = hospitalSetService.removeByIds(idList);
        if (batchRemove) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @ApiOperation(value = "修改医院设置")
    @PostMapping("update")
    public R update(@RequestBody HospitalSet hospitalSet) {
        boolean update = hospitalSetService.updateById(hospitalSet);
        if (update) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @ApiOperation(value = "根据id查询医院设置")
    @GetMapping("getById/{id}")
    public R getById(@PathVariable("id") Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return R.ok().data("hospitalSet", hospitalSet);
    }

    @ApiOperation(value = "新增医院设置")
    @PostMapping("save")
    public R save(@RequestBody HospitalSet hospitalSet) {
        boolean save = hospitalSetService.save(hospitalSet);
        if (save) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @ApiOperation(value = "带分页带条件查询医院设置")
    @PostMapping("pageQuery/{page}/{limit}")
    public R pageList(@PathVariable("page") Long page,
                      @PathVariable Long limit,
                      @RequestBody HospitalSetQueryVo hospitalSetQueryVo) {
        //1.获取参数，验空，存入查询条件构造器
        String hosname = hospitalSetQueryVo.getHosname();
        String hoscode = hospitalSetQueryVo.getHoscode();
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(hosname)) {
            wrapper.like("hosname", hosname);
        }
        if (!StringUtils.isEmpty(hoscode)) {
            wrapper.eq("hoscode", hoscode);
        }
        //2.创建分页查询对象
        Page<HospitalSet> pageParam = new Page<>(page, limit);
        //3.分页查询
        Page<HospitalSet> pageModel = hospitalSetService.page(pageParam, wrapper);
        return R.ok().data("pageModel", pageModel);
    }

    @ApiOperation(value = "分页查询医院设置")
    @GetMapping("{page}/{limit}")
    public R pageList(@PathVariable("page") Long page, @PathVariable Long limit) {
        //1.创建分页查询对象
        Page<HospitalSet> pageParam = new Page<>(page, limit);
        //2.分页查询
        Page<HospitalSet> pageModel = hospitalSetService.page(pageParam);
        return R.ok().data("pageModel", pageModel);
    }

    //删除方法
    //@RequestMapping("{id}")
    @ApiOperation(value = "医院设置删除")
    @DeleteMapping("{id}")
    public R removeById(@PathVariable("id") Long id) {
        boolean remove = hospitalSetService.removeById(id);
        return R.ok();
        //return findAll();
    }

    //查询所有医院设置
    @ApiOperation(value = "医院设置列表")
    @GetMapping("findAll")
    public R findAll() {
        /*try {
            int a = 10 / 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new YyghException(20001, "出现自定义的异常");
        }*/
        List<HospitalSet> list = hospitalSetService.list();
        return R.ok().data("list", list);
    }

}
