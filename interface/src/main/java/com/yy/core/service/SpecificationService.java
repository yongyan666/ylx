package com.yy.core.service;

import com.yy.core.entity.PageResult;
import com.yy.core.entity.SpecEntity;
import com.yy.core.pojo.specification.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService {
    // 添加
    public void add(SpecEntity specEntity);
    // 查询所有带分页  带条件
    public PageResult findPage(Specification spec, Integer page, Integer rows);
    //根据id回显信息 查询
    public SpecEntity findOne(Long id);
    //更新
    public void update(SpecEntity specEntity);
    //删除
    public void delete(Long[] ids);

    public List<Map> selectOptionList();
}
