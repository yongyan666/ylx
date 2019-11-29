package com.yy.core.service;

import com.yy.core.entity.PageResult;
import com.yy.core.pojo.good.Brand;
import com.yy.core.pojo.template.TypeTemplate;

import java.util.List;
import java.util.Map;

public interface TemplateService {
    //增加
    public void add(TypeTemplate template);
    //修改
    public void update(TypeTemplate template);
    // 查询
    public PageResult findPage(TypeTemplate template,Integer pageNum,Integer pageSize);
    //批量删除
    public void delete(Long[] ids);

    public TypeTemplate findOne(Long id);
    //模板下拉框数据
    public List<Map> selectOptionList();

    public List<Map> findBySpecList(Long id);
}
