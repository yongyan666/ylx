package com.yy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yy.core.entity.PageResult;
import com.yy.core.entity.Result;
import com.yy.core.pojo.seller.Seller;
import com.yy.core.service.SellerService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController{
    @Reference
    private SellerService sellerService;
    //商家注册
    @RequestMapping(value = "/add")
    public Result add(@RequestBody Seller seller){
        try{
            sellerService.add(seller);
            return new Result(true,"注册成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"注册失败");
        }
    }
    @RequestMapping("/updateStatus")
    public Result updateStatus(String sellerId,String status){
        try {
            sellerService.updateStatus(sellerId, status);
            return new Result(true, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "失败");
        }
    }
    @RequestMapping("/search")
    public PageResult search(@RequestBody Seller seller, Integer page, Integer rows){
            return sellerService.findPage(seller,page,rows);
    }
}
