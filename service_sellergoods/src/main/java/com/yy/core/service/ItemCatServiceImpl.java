package com.yy.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.yy.core.dao.item.ItemCatDao;
import com.yy.core.pojo.item.ItemCat;
import com.yy.core.pojo.item.ItemCatQuery;
import com.yy.core.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
@Service
public class ItemCatServiceImpl implements ItemCatService{
    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<ItemCat> findByParentId(Long parentId) {
        // 获取所有的分类数据
        List<ItemCat> itemCatAll = itemCatDao.selectByExample(null);
        //分类名称作为key 模板的id 作为值
        for(ItemCat itemCat:itemCatAll){
            redisTemplate.boundHashOps(Constants.CATEGORY_LIST_REDIS).put(itemCat.getName(),itemCat.getTypeId());
        }
        // 根据父级id 查询它的子集  展示到页面
        ItemCatQuery query = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = query.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<ItemCat> itemCats = itemCatDao.selectByExample(query);
        return itemCats;
    }

    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(ItemCat itemCat) {
        itemCatDao.updateByPrimaryKey(itemCat);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id:ids){
            itemCatDao.deleteByPrimaryKey(id);
        }
    }
//根据条件
    @Override
    public List<ItemCat> findAll() {
        return itemCatDao.selectByExample(null);
    }
}
