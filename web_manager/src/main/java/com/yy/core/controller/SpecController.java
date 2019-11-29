package com.yy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yy.core.entity.PageResult;
import com.yy.core.entity.Result;
import com.yy.core.entity.SpecEntity;
import com.yy.core.pojo.specification.Specification;
import com.yy.core.service.SpecificationService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.rmi.MarshalledObject;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specification")
public class SpecController {
    @Reference
    private SpecificationService specService;
    //分页查询
    @RequestMapping("/search")
    public PageResult search(@RequestBody Specification spec,Integer page,Integer rows){
        PageResult result=specService.findPage(spec,page,rows);
        return result;
    }
    // 规格添加
    @RequestMapping("/add")
    public Result add(@RequestBody SpecEntity specEntity){
        try{
            specService.add(specEntity);
            return new Result(true,"保存成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"保存失败");
        }
    }
    //修改
    @RequestMapping("/findOne")
    public SpecEntity findOne(Long id){
        SpecEntity one=specService.findOne(id);
        return one;
    }
    //更新
    @RequestMapping("update")
    public Result update(@RequestBody SpecEntity specEntity){
        try{
            specService.update(specEntity);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }
    //删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            specService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
    // 模板模块下拉 规格菜单
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return specService.selectOptionList();
    }

}
