package com.yy.core.service;

import com.yy.core.entity.PageResult;
import com.yy.core.pojo.good.Brand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    //查询所有
    public List<Brand> findAll();
    //分页
    public PageResult findPage(Integer pageNum,Integer pageSize);
     //增加
    public void add(Brand brand);
    //修改
    public void update(Brand brand);
    /**
     * 根据ID获取实体
     * @param id
     * @return
     */
    public Brand findOne(Long id);
    // 查询
    public PageResult findPage(Brand brand,int pageNum,int pageSize);
    //批量删除
    public void delete(Long[] ids);
    //  模板回显数据 品牌下拉菜单
    public List<Map> selectOptionList();
}
