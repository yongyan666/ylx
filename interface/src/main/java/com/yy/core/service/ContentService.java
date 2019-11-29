package com.yy.core.service;

import com.yy.core.entity.PageResult;
import com.yy.core.pojo.ad.Content;

import java.util.List;

public interface ContentService {
    // 查所有
    public List<Content> findAll();
    // 分页
    public PageResult findPage(Content content, Integer page, Integer rows);
    // 添加
    public void add(Content content);
    // 修改
    public Content findOne(Long id);
    public void update(Content content);

    //删除
    public void delete(Long[] ids);
    // 广告查询
    public List<Content> findByCategoryId(Long categoryId);

    public List<Content> findByCategoryIdFromRedis(Long categoryId);
}
