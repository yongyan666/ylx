package com.yy.core.service;

import com.yy.core.pojo.item.Item;
import com.yy.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;


import java.util.*;


@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Map<String, Object> search(Map paramMap) {
        // 1 根据参数关键字  到solr 中查询（分页）过滤 总条数  总页数
        Map<String, Object> resultMap = highlightSearch(paramMap);
        //2 根据查询的参数 道solr中获取对应的分类结果 因为分类有重复  按分组的方式去重复
        List<String> groupCatgroupList = findGroupCatgroupList(paramMap);
        resultMap.put("categoryList",groupCatgroupList);
        // 3 判断paramMap传入的参数中是否有分类的名称
        String category = String.valueOf(paramMap.get("category"));
        if(category!=null&&!"".equals(category)){
            //5 如果有分类参数  则根据分类查询对应的品牌集合和规格集合
            Map specListAndBrandList = findSpecListAndBrandList(category);
            resultMap.putAll(specListAndBrandList);
        }else {
            //4 如果没有 根据第一个分类查询对应的商品集合
            Map specListAndBrandList = findSpecListAndBrandList(groupCatgroupList.get(0));
            resultMap.putAll(specListAndBrandList);
        }


        return resultMap;
    }
    // 1 根据参数关键字  到solr 中查询（分页） 总条数  总页数
    private Map<String, Object> highlightSearch(Map paramMap){
        // 获取关键字
        String keywords = String.valueOf(paramMap.get("keywords"));
        if(keywords!=null){
            keywords = keywords.replaceAll(" ", "");
        }
        // 当前页
        Integer pageNo = Integer.parseInt(String.valueOf(paramMap.get("pageNo")));
        // 没页查询多少条
        Integer pageSize = Integer.parseInt(String.valueOf(paramMap.get("pageSize")));
        //获取点击分类过滤条件
        String category = String.valueOf(paramMap.get("category"));
        // 获取页面点击品牌的过滤条件
        String brand = String.valueOf(paramMap.get("brand"));
        // System.out.println(brand);
        // 获取页面点击规格的过滤条件
        String spec = String.valueOf(paramMap.get("spec"));
        // 获取页面点击的价格区间的过滤条件
        String price = String.valueOf(paramMap.get("price"));
        //获取页面传入的排序的域
        String sortField = String.valueOf(paramMap.get("sortField"));
        // 获取页面传入的排序方式
        String sortType= String.valueOf(paramMap.get("sort"));
        // 封装查询对象
        HighlightQuery query = new SimpleHighlightQuery();
        //查询的条件对象
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        // 将查询条件放入对象中
        query.addCriteria(criteria);
        //计算从第几条开始查询
        if(pageNo==null||pageNo<=0){
            pageNo=1;
        }
        Integer start = (pageNo - 1) * pageSize;
        // 设置从第几天查询
        query.setOffset(start);
        // 设置每页多少条
        query.setRows(pageSize);


        //创建高亮显示对象
        HighlightOptions highlightOptions = new HighlightOptions();
        // 设置哪个域需要高亮显示
        highlightOptions.addField("item_title");
        // 高亮的前缀
        highlightOptions.setSimplePrefix("<em style=\"color:red\">");
        // 高亮的后缀
        highlightOptions.setSimplePostfix("</em>");
        // 将高亮假如到查询对象中
        query.setHighlightOptions(highlightOptions);

        //根据分类查询
        if(category!=null&&!"".equals(category)){
            // 创建查询对象
            FilterQuery filterQuery = new SimpleFilterQuery();
            // 创建查询条件

            Criteria filterCriteria = new Criteria("item_category").is(category);
            // 过滤对象放入到查询对象中
            filterQuery.addCriteria(filterCriteria);
            //过滤对象放入查询对象中
            query.addFilterQuery(filterQuery);
        }
        // 根据品牌过滤查询
        if(brand!=null&&!"".equals(brand)){
            // 创建查询对象
            FilterQuery filterQuery = new SimpleFilterQuery();
            // 创建查询条件

            Criteria filterCriteria = new Criteria("item_brand").is(brand);
            // 条件对象放入到查询对象中
            filterQuery.addCriteria(filterCriteria);
            //过滤对象放入查询对象中
            query.addFilterQuery(filterQuery);

        }
        //根据规格查询   spec中数据格式{网络：移动3G，内存：16G}
        if(spec!=null&&!"".equals(spec)){
            Map<String,String> speMap = JSON.parseObject(spec, Map.class);
            if(speMap!=null&&speMap.size()>0){
                Set<Map.Entry<String, String>> entries = speMap.entrySet();
                for(Map.Entry<String,String> entry:entries ){
                    //创建过滤查询对象
                    FilterQuery filterQuery = new SimpleFilterQuery();
                    // 创建查询条件
                    Criteria filterCriteria = new Criteria("item_spec_"+entry.getKey()).is(entry.getValue());
                    //将条件对象放入到过滤对象中
                    filterQuery.addCriteria(filterCriteria);
                    // 将过滤对象 放入查询对象中
                    query.addFilterQuery(filterQuery);

                }
            }
        }
        // 根据价格查询
        if(price!=null&&!"".equals(price)){
            // 切分价格
            String[] split = price.split("-");

            if(split!=null&&split.length==2){

                // 说明大于等于最小值  如果第一个值为零 进不来
                if(!"0".equals(split[0])){
                    //创建过滤查询对象
                    FilterQuery filterQuery = new SimpleFilterQuery();
                    // 创建查询条件
                    Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(split[0]);
                    //将条件对象放入到过滤对象中
                    filterQuery.addCriteria(filterCriteria);
                    // 将过滤对象 放入查询对象中
                    query.addFilterQuery(filterQuery);
                }
                //小于等于最大值  如果最后的元素为*  *最大值  进入不到这里
                if(!"*".equals(split[1])){
                    //创建过滤查询对象
                    FilterQuery filterQuery = new SimpleFilterQuery();
                    // 创建查询条件
                    Criteria filterCriteria = new Criteria("item_price").lessThanEqual(split[1]);
                    //将条件对象放入到过滤对象中
                    filterQuery.addCriteria(filterCriteria);
                    // 将过滤对象 放入查询对象中
                    query.addFilterQuery(filterQuery);
                }
            }
        }
        // 添加排序条件
        if(sortField!=null&&sortType!=null&&!"".equals(sortField)&&!"".equals(sortType)){
            //升序排序
            if("ASC".equals(sortType)){
                // 创建排序对象   枚举 一组常量的值                      价格域    从页面传回来的价格
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                // 将排序对象 放入查询对象中
                query.addSort(sort);
            }
            // 降序排序
            if("DESC".equals(sortType)){
                // 创建排序对象   枚举 一组常量的值                      价格域    从页面传回来的价格
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                // 将排序对象 放入查询对象中
                query.addSort(sort);
            }
        }
        // 查询并且返回结果
        HighlightPage<Item> items = solrTemplate.queryForHighlightPage(query, Item.class);

        //获取带高亮的集合
        List<HighlightEntry<Item>> highlighted = items.getHighlighted();
        List<Item> itemList = new ArrayList<>();
        //遍历高亮集合
        for(HighlightEntry<Item> itemHighlightEntry:highlighted){
            Item item = itemHighlightEntry.getEntity();
            List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();
            if(highlights!=null&&highlights.size()>0){
                // 获取高亮的标题集合
                List<String> highlightTitle = highlights.get(0).getSnipplets();
                if(highlightTitle!=null&&highlightTitle.size()>0){
                    // 获取高亮的标题
                    String title = highlightTitle.get(0);
                    item.setTitle(title);
                }
            }
            itemList.add(item);
        }
        Map<String, Object> resultMap = new HashMap<>();
        //查询到的结果集
        resultMap.put("rows",itemList);
        // 总页数
        resultMap.put("totalPage",items.getTotalPages());
        // 总条数
        resultMap.put("total",items.getTotalElements());
        return resultMap;
    }
    //2 根据查询的参数 道solr中获取对应的分类结果 因为分类有重复  按分组的方式去重复
    private List<String> findGroupCatgroupList(Map paramMap){
        List<String> resultList = new ArrayList<>();
        // 获取关键字
        String keywords = String.valueOf(paramMap.get("keywords"));
        if(keywords!=null){
            keywords = keywords.replaceAll(" ", "");
        }
        // 创建查询对象
        SimpleQuery query = new SimpleQuery();
        // 创建查询条件对象
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        // 将查询的条件放入道查询对象中
        query.addCriteria(criteria);

        //创建分组对象
        GroupOptions groupOptions = new GroupOptions();
        //设置根据分类域进行分组
        groupOptions.addGroupByField("item_category");
        // 将分组对象放入查询对象中
        query.setGroupOptions(groupOptions);
        // 使用分组查询    分类集合
        GroupPage<Item> items = solrTemplate.queryForGroupPage(query, Item.class);
        // 获得结果集合  分类域集合
        GroupResult<Item> item_category = items.getGroupResult("item_category");
        //获得分类域中的实体集合
        Page<GroupEntry<Item>> groupEntries = item_category.getGroupEntries();
        // 遍历实体集合  得到实体对象
        for(GroupEntry<Item> groupEntry:groupEntries){
            String groupCategory = groupEntry.getGroupValue();
            // 组装到集合中
            resultList.add(groupCategory);
        }

        return resultList;


    }
    //4 根据分类名称查询对应品牌集合和规格集合
    private Map findSpecListAndBrandList(String categoryName){
        //a 根据分类名称到redis中查询对应的模板id
        Long templateId = (Long)redisTemplate.boundHashOps(Constants.CATEGORY_LIST_REDIS).get(categoryName);
        //b根据模板id  去redis中查询对应的品牌集合
        List<Map> brandList = (List<Map>)redisTemplate.boundHashOps(Constants.BRAND_LIST_REDIS).get(templateId);
        System.out.println(brandList);
        //b根据模板id  去redis中查询对应的规格集合
        List<Map> specList =(List<Map>) redisTemplate.boundHashOps(Constants.SPEC_LIST_REDIS).get(templateId);
        //a 将品牌集合和规格集合封装到Map中 返回
        Map resultMap = new HashMap();
        resultMap.put("brandList",brandList);
        resultMap.put("specList",specList);
        return resultMap;


    }
}
