package com.yy.core.service;


import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yy.core.dao.good.BrandDao;
import com.yy.core.entity.PageResult;
import com.yy.core.pojo.good.Brand;
import com.yy.core.pojo.good.BrandQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandDao brandDao;
    @Override
    public List<Brand> findAll() {
        List<Brand> brands = brandDao.selectByExample(null);
        return brands;
    }

    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Page<Brand> page = (Page<Brand>)brandDao.selectByExample(null);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void add(Brand brand) {
        brandDao.insert(brand);
    }

    /**
     * 修改
     */

    @Override
    public void update(Brand brand) {
        brandDao.updateByPrimaryKey(brand);
    }

    /**
     * 根据ID获取实体
     * @param id
     * @return
     */

    @Override
    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    @Override
    public PageResult findPage(Brand brand, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        BrandQuery example=new BrandQuery();
        BrandQuery.Criteria criteria = example.createCriteria();
        if(brand!=null){
            if(brand.getName()!=null && brand.getName().length()>0){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if(brand.getFirstChar()!=null && brand.getFirstChar().length()>0){
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }
        Page<Brand> page= (Page<Brand>)brandDao.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void delete(Long[] ids) {
       for (Long id:ids){
           brandDao.deleteByPrimaryKey(id);
       }
    }

    @Override
    public List<Map> selectOptionList() {
       return brandDao.selectOptionList();
    }

}