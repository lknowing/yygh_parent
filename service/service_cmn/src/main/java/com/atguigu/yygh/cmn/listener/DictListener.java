package com.atguigu.yygh.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/19 14:42
 * @FileName: DictListener
 */
@Component
public class DictListener extends AnalysisEventListener<DictEeVo> {
    @Autowired
    private DictMapper dictMapper;

    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext context) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo, dict);
        dict.setIsDeleted(0);
        dictMapper.insert(dict);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}
