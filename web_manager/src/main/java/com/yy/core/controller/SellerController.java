package com.yy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yy.core.entity.PageResult;
import com.yy.core.entity.Result;
import com.yy.core.pojo.seller.Seller;
import com.yy.core.service.SellerService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {
    @Reference
    private SellerService sellerService;
    @RequestMapping("/search")
    public PageResult search(@RequestBody Seller seller, Integer page, Integer rows){
        PageResult result = sellerService.findPage(seller, page, rows);
        return result;
    }
    // 数据回显
    @RequestMapping("/findOne")
    public Seller findOne(String id){
        return sellerService.findOne(id);
    }
    @RequestMapping("/updateStatus")
    public Result updateStatus(String sellerId, String status){
        try{
            sellerService.updateStatus(sellerId,status);
            return new Result(true,"修改状态成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(true,"修改状态失败");
        }
    }

}