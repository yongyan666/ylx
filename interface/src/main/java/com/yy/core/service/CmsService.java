package com.yy.core.service;

import java.util.Map;

/**
 * @auther 闫永
 * @date2019/11/26 14:11
 */
public interface CmsService {
    //取数据
    public Map<String,Object> findGoodsData(Long goodsId);
    //根据取到的数据 生成页面
    public void createStaticPage(Long goodsId,Map<String,Object> rootMap)throws Exception;
}
