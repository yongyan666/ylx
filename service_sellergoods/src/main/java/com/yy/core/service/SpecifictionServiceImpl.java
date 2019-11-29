package com.yy.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yy.core.dao.specification.SpecificationDao;
import com.yy.core.dao.specification.SpecificationOptionDao;
import com.yy.core.entity.PageResult;
import com.yy.core.entity.SpecEntity;
import com.yy.core.pojo.specification.Specification;
import com.yy.core.pojo.specification.SpecificationOption;
import com.yy.core.pojo.specification.SpecificationOptionQuery;
import com.yy.core.pojo.specification.SpecificationQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecifictionServiceImpl implements SpecificationService{
    @Autowired
    private SpecificationDao specDao;
    @Autowired
    private SpecificationOptionDao optionDao;
    @Override
    public PageResult findPage(Specification spec, Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        SpecificationQuery query = new SpecificationQuery();
        SpecificationQuery.Criteria criteria = query.createCriteria();
        if(spec!=null){
            if(spec.getSpecName()!=null&&!"".equals(spec.getSpecName())){
                criteria.andSpecNameLike("%"+spec.getSpecName()+"%");
            }
        }
        Page<Specification> specList =(Page<Specification>) specDao.selectByExample(query);
        return new PageResult(specList.getTotal(),specList.getResult());
    }

    @Override
    public void add(SpecEntity specEntity) {
        //1. 添加规格对象
        specDao.insertSelective(specEntity.getSpecification());

        //2. 添加规格选项对象
        if (specEntity.getSpecificationOptionList() != null) {
            for (SpecificationOption option : specEntity.getSpecificationOptionList()){
                //设置规格选项外键
                option.setSpecId(specEntity.getSpecification().getId());
                optionDao.insertSelective(option);
            }
        }
    }

    @Override
    public SpecEntity findOne(Long id) {
        // 1 根据规格id 查规格对象
        Specification spec = specDao.selectByPrimaryKey(id);
        //2 根据规格id 查询规格选项集合  外键要组装到查询到条件中   形成查询对象
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = query.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<SpecificationOption> optionList = optionDao.selectByExample(query);
        //3  将规格对象和规格选项集合对象 封装到返回实体对象中
        SpecEntity specEntity = new SpecEntity();
        specEntity.setSpecification(spec);
        specEntity.setSpecificationOptionList(optionList);
        return specEntity;
    }
    //  更新操作
    @Override
    public void update(SpecEntity specEntity) {
        //  1 根据规格对象进行更新
        specDao.updateByPrimaryKeySelective(specEntity.getSpecification());
        //  2 根据规格id 删除对应的规格选项集合数据
        //2.1  查询
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = query.createCriteria();
        criteria.andSpecIdEqualTo(specEntity.getSpecification().getId());
        optionDao.deleteByExample(query);
        //  3  将新的规格选项集合添加到规格选项表中
        if(specEntity.getSpecificationOptionList()!=null){
            for(SpecificationOption option:specEntity.getSpecificationOptionList()){
                // 设置选项对象的外键
                option.setSpecId(specEntity.getSpecification().getId());
                optionDao.insertSelective(option);
            }
        }
    }

    @Override
    public void delete(Long[] ids) {
        if(ids!=null){
            for(Long id:ids){
                // 1  根据规格id   删除规格对象
                specDao.deleteByPrimaryKey(id);
                //2  根据规格的id 删除规格的选项集合
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = query.createCriteria();
                criteria.andSpecIdEqualTo(id);
                optionDao.deleteByExample(query);

            }
        }
    }

    @Override
    public List<Map> selectOptionList() {
        return specDao.selectOptionList();
    }


}