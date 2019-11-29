package com.yy.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.yy.core.dao.item.ItemDao;
import com.yy.core.pojo.item.Item;
import com.yy.core.pojo.item.ItemQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;

import java.util.List;
import java.util.Map;


@Service
public class SolrManagerServiceImpl implements SolrManagerService{
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void saveItemToSolr(Long id) {
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        // 查询指定的商品的库存数据
        criteria.andGoodsIdEqualTo(id);
        List<Item> items = itemDao.selectByExample(query);
        if(items!=null){
            for(Item item:items){
                // 获得规格json格式
                String specJsonStr = item.getSpec();
                Map map = JSON.parseObject(specJsonStr, Map.class);
                item.setSpecMap(map);
            }
            solrTemplate.saveBeans(items);
            solrTemplate.commit();
        }
    }

    @Override
    public void deleteItemFromSolr(Long id) {
        // 创建查询对象
        SimpleQuery query = new SimpleQuery();
        // 创建条件对象
        Criteria criteria = new Criteria("item_goodsid").is(id);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
