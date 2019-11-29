package com.yy.core.util;

import com.alibaba.fastjson.JSON;
import com.yy.core.dao.item.ItemDao;
import com.yy.core.pojo.item.Item;
import com.yy.core.pojo.item.ItemQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @auther 闫永
 * @date2019/11/21 19:05
 */
@Component
public class DataImportToSolr {
    @Autowired
    // 查询库存表中的所有数据
    private ItemDao itemDao;
    @Autowired
    private SolrTemplate solrTemplate;
    private void importItemDataToSolr() {
        ItemQuery query = new ItemQuery();
        // mybatis 查询
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andStatusEqualTo("1");
        List<Item> items = itemDao.selectByExample(query);
        if (items!=null){
            for (Item item:items){
                //  获取规格  json 字符串
                String specJsonStr = item.getSpec();
                Map map = JSON.parseObject(specJsonStr, Map.class);
                item.setSpecMap(map);
            }
            // 放入SOLR
            solrTemplate.saveBeans(items);
            //提交
            solrTemplate.commit();
        }
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        DataImportToSolr bean = (DataImportToSolr)context.getBean("dataImportToSolr");
        bean.importItemDataToSolr();
    }
}
