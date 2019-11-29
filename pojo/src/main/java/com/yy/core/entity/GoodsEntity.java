package com.yy.core.entity;

import com.yy.core.pojo.good.Goods;
import com.yy.core.pojo.good.GoodsDesc;
import com.yy.core.pojo.item.Item;

import java.io.Serializable;
import java.util.List;

public class GoodsEntity implements Serializable {
    //    商品对象
    private Goods goods;

    //    商品详情对象
    private GoodsDesc goodsDesc;
    //    库存对象
    private List<Item> itemList;

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public GoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(GoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }
}
