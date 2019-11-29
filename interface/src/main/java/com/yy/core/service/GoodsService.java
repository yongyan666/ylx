package com.yy.core.service;


import com.yy.core.entity.*;
import com.yy.core.pojo.good.Goods;


/**
 * @author lijun
 * @date 2019/11/18 14:49
 */
public interface GoodsService {
    public void  add(GoodsEntity goodsEntity);

    public PageResult findPage(Goods goods, Integer page, Integer rows);

    public GoodsEntity findOne(Long id);

    public void update(GoodsEntity goodsEntity);

    public void updateStatus(Long id, String status);

    public void delete(Long id);
}
