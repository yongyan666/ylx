package com.yy.core.service;

import com.yy.core.entity.PageResult;
import com.yy.core.pojo.seller.Seller;

public interface SellerService {
    public void add(Seller seller);
    //修改状态
    public void updateStatus(String sellerId,String status);
    //分页 查
    public PageResult findPage(Seller seller,Integer page,Integer rows);
    // 数据回显
    public Seller findOne(String id);
}
