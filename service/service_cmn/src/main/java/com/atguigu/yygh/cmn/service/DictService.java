package com.atguigu.yygh.cmn.service;

import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/19 09:19
 * @FileName: DictService
 */
public interface DictService extends IService<Dict> {
    //根据数据id查询子数据列表
    List<Dict> findChildData(Long id);

    //导出字典数据
    void exportData(HttpServletResponse response);

    //导入字典数据
    void importData(MultipartFile file);

    /**
     * 根据上级编码与值获取数据字典名称
     *
     * @param parentDictCode
     * @param value
     * @return
     */
    String getName(String parentDictCode, String value);

    /**
     * 根据dictCode获取下级节点
     * @param dictCode
     * @return
     */
    List<Dict> findByDictCode(String dictCode);
}
