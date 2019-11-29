package com.yy.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yy.core.dao.specification.SpecificationOptionDao;
import com.yy.core.dao.template.TypeTemplateDao;
import com.yy.core.entity.PageResult;
import com.yy.core.pojo.specification.SpecificationOption;
import com.yy.core.pojo.specification.SpecificationOptionQuery;
import com.yy.core.pojo.template.TypeTemplate;
import com.yy.core.pojo.template.TypeTemplateQuery;
import com.yy.core.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TemplateServiceImpl implements TemplateService{
    @Autowired
    private TypeTemplateDao templateDao;
    @Autowired
    private SpecificationOptionDao optionDao;
    @Autowired
    private RedisTemplate redisTemplate;
    //添加
    @Override
    public void add(TypeTemplate template) {
        templateDao.insert(template);
    }

    //修改
    @Override
    public void update(TypeTemplate template) {
        templateDao.updateByPrimaryKeySelective(template);
    }

    @Override
    public PageResult findPage(TypeTemplate template, Integer page, Integer rows) {
        // redis 中缓存模板的所有数据
        List<TypeTemplate> typeTempAll = templateDao.selectByExample(null);
        // 模板的id 作为键  品牌集合作为value 存入redis中
        for(TypeTemplate typeTemplate:typeTempAll){
            String brandIdsJsonStr = typeTemplate.getBrandIds();
            // 将json 转成集合存进去
            List<Map> brandList = JSON.parseArray(brandIdsJsonStr, Map.class);
            redisTemplate.boundHashOps(Constants.BRAND_LIST_REDIS).put(typeTemplate.getId(),brandList);
            //  // 模板的id 作为键  规格集合作为value 存入redis中
            List<Map> specList = findBySpecList(typeTemplate.getId());
            redisTemplate.boundHashOps(Constants.SPEC_LIST_REDIS).put(typeTemplate.getId(),specList);
        }
        // 模板分页
        PageHelper.startPage(page,rows);
        TypeTemplateQuery query = new TypeTemplateQuery();
        TypeTemplateQuery.Criteria criteria = query.createCriteria();
        if(template!=null){
            if(template.getName()!=null&&!"".equals(template.getName())){
                criteria.andNameLike("%"+template.getName()+"%");
            }
        }
        Page<TypeTemplate> templateList =(Page<TypeTemplate>) templateDao.selectByExample(query);
        return new PageResult(templateList.getTotal(),templateList.getResult());
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id:ids){
            templateDao.deleteByPrimaryKey(id);
        }
    }
    //回显数据
    @Override
    public TypeTemplate findOne(Long id) {
        return templateDao.selectByPrimaryKey(id);
    }

    @Override
    public List<Map> selectOptionList() {
        return templateDao.selectOptionList();
    }

    @Override
    public List<Map> findBySpecList(Long id) {
        TypeTemplate typeTemplate = templateDao.selectByPrimaryKey(id);
        List<Map> maps = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
        //4 遍历集合对象
        if(maps!=null){

            for(Map map:maps){
                // 5 遍历 根据规格id  查询对应的规格选项数据
                Long specId = Long.parseLong(String.valueOf(map.get("id")));
                //6  将规格选项  再封装道规格选项中 一起返回
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = query.createCriteria();
                criteria.andSpecIdEqualTo(specId);
                // 根据规格id  获得规格选项数据
                List<SpecificationOption> optionList = optionDao.selectByExample(query);
                // 将规格选项集合封装到原来的map 中
                map.put("options",optionList);

            }

        }
        return maps;
    }
}
