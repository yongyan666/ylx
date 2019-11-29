package com.yy.core.service;

import com.yy.core.entity.PageResult;
import com.yy.core.pojo.ad.ContentCategory;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


public interface CategoryService {
    //显示数据
    public List<ContentCategory> findAll();
    //分页
    public PageResult findPage(Integer page, Integer rows);
    //添加
    public void add(ContentCategory category);
    //回显修改
    public ContentCategory findOne(Long id);
    public void update(ContentCategory category);
    //删除
    public void delete(Long[] ids);
    public PageResult findPage(@RequestBody ContentCategory category, Integer page, Integer rows);
}
