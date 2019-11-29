package com.yy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yy.core.entity.PageResult;
import com.yy.core.entity.Result;
import com.yy.core.pojo.good.Brand;
import com.yy.core.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;
    @RequestMapping("/findAll")
    public List<Brand> findaAll(){
        List<Brand> list = brandService.findAll();
        return list;
    }
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page,Integer rows){
        return brandService.findPage(page,rows);
    }
    @RequestMapping("/add")
    public Result add(@RequestBody Brand brand){
        try{
            brandService.add(brand);
            return new Result(true,"成功");
        }catch (Exception e){
            return new Result(false,"失败");
        }

    }
    @RequestMapping("update")
    public Result update(@RequestBody Brand brand){
        try{
            brandService.update(brand);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }
    @RequestMapping("/findOne")
    public Brand findOne(Long id){
        return brandService.findOne(id);
    }
    @RequestMapping("/search")
    public PageResult search(@RequestBody Brand brand, int page, int rows  ){
        return brandService.findPage(brand, page, rows);
    }
    // 删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            brandService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }

    }
    //  获取模板下拉数据
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }
}
