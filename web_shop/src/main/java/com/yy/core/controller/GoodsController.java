package com.yy.core.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.yy.core.entity.GoodsEntity;
import com.yy.core.entity.PageResult;
import com.yy.core.entity.Result;
import com.yy.core.pojo.good.Goods;
import com.yy.core.service.GoodsService;
import com.yy.core.service.SolrManagerService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Reference
    private GoodsService goodsService;
   /* @Reference
    private SolrManagerService solrManagerService;*/
    @RequestMapping("/add")
    public Result add(@RequestBody GoodsEntity goodsEntity){
        System.out.println(goodsEntity);
        try{
            //  卖家名称初始化 不让用户填写
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            goodsEntity.getGoods().setSellerId(userName);
            goodsService.add(goodsEntity);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }

    }
    @RequestMapping("/search")
    public PageResult search(@RequestBody Goods goods,Integer page,Integer rows){
        //获取当前用户的邓丽的用户名
        String username =SecurityContextHolder.getContext().getAuthentication().getName();
        //添加查询条件、
        goods.setSellerId(username);
        PageResult result = goodsService.findPage(goods, page, rows);
        return result;
    }
    @RequestMapping("/findOne")
    public GoodsEntity findOne(Long id){
        return goodsService.findOne(id);
    }
    @RequestMapping("/update")
    public Result update(@RequestBody GoodsEntity goodsEntity) {
        try {
            // 获取当前的登录名
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            // 商品的所有者
            String sellerId = goodsEntity.getGoods().getSellerId();
            if (!userName.equals(sellerId)) {
                return new Result(false, "您没有权限修改此商品");
            }
            goodsService.update(goodsEntity);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }
    // 删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            if(ids!=null){
                for(Long id:ids){
                    goodsService.delete(id);
                    // 根据商品id  删除solr索引库中的数据
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
