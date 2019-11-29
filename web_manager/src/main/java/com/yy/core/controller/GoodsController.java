package com.yy.core.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.yy.core.entity.GoodsEntity;
import com.yy.core.entity.PageResult;
import com.yy.core.entity.Result;
import com.yy.core.pojo.good.Goods;
import com.yy.core.service.CmsService;
import com.yy.core.service.GoodsService;
import com.yy.core.service.SolrManagerService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Reference
    private GoodsService goodsService;
    /*@Reference
    private SolrManagerService solrManagerService;
    @Reference
    private CmsService cmsService;*/
    @RequestMapping("/search")
    public PageResult search(@RequestBody Goods goods,Integer page,Integer rows){
        //  获取当前用户登录的用户名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(userName);
        PageResult result = goodsService.findPage(goods, page, rows);
        return result;
    }
    // 回显数据
    @RequestMapping("/findOne")
    public GoodsEntity findOne(Long id){
        return goodsService.findOne(id);
    }
    @RequestMapping("/update")
    public Result update(@RequestBody GoodsEntity goodsEntity){
        try{
            // 获取当前的登录名
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            // 商品的所有者
            String sellerId = goodsEntity.getGoods().getSellerId();
            if(!userName.equals(sellerId)){
                return new Result(false,"您没有权限修改此商品");
            }
            goodsService.update(goodsEntity);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }
    // 修改状态
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids,String status){
        try{
            if(ids!=null){
                for(Long id:ids){
                    // 1 根据商品的id 改变商品的上架的状态
                    goodsService.updateStatus(id,status);
                    // 2根据商品的id 到solr索引库中删除对应的数据
                    /*if("1".equals(status)){
                        //3 根据商品id获取库存数据  放入solr 索引库中 提供给搜索使用
                        solrManagerService.saveItemToSolr(id);
                        //4 根据商品的id 获取商品的详情数据  并且根据详情数据和模板生成详情的页面
                        Map<String, Object> goodsData = cmsService.findGoodsData(id);
                        cmsService.createStaticPage(id,goodsData);
                    }*/
                }
            }

            return new Result(true,"状态修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"状态修改失败");
        }
    }
   /* //测试生成静态页面
    @RequestMapping("/testPage")
    public Boolean testCreatePage(Long goodsId){
        try{
            Map<String, Object> goodsData = cmsService.findGoodsData(goodsId);
            cmsService.createStaticPage(goodsId,goodsData);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }*/
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            if(ids!=null){
                for(Long id: ids){
                    goodsService.delete(id);
                    //solrManagerService.deleteItemFromSolr(id);
                }

            }
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
}