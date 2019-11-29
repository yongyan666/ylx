package com.yy.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yy.core.dao.ad.ContentCategoryDao;
import com.yy.core.entity.PageResult;
import com.yy.core.pojo.ad.ContentCategory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    private ContentCategoryDao categoryDao;
    @Override
    public List<ContentCategory> findAll() {
        return categoryDao.selectByExample(null);
    }

    @Override
    public PageResult findPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        Page<ContentCategory> categories = (Page<ContentCategory>)categoryDao.selectByExample(null);
        return new PageResult(categories.getTotal(),categories.getResult());
    }

    @Override
    public void add(ContentCategory category) {
        categoryDao.insert(category);
    }

    @Override
    public ContentCategory findOne(Long id) {
        return categoryDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(ContentCategory category) {
        categoryDao.updateByPrimaryKey(category);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id:ids){
            categoryDao.deleteByPrimaryKey(id);
        }
    }

    @Override
    public PageResult findPage(ContentCategory category, Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        Page<ContentCategory> categories = (Page<ContentCategory>)categoryDao.selectByExample(null);
        return new PageResult(categories.getTotal(),categories.getResult());
    }
}
