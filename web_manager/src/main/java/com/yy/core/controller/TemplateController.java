package com.yy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yy.core.entity.PageResult;
import com.yy.core.entity.Result;
import com.yy.core.pojo.template.TypeTemplate;
import com.yy.core.service.TemplateService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TemplateController {
    @Reference
    private TemplateService templateService;

    //增加
    @RequestMapping("/add")
    public Result add(@RequestBody TypeTemplate template){
        try{
            templateService.add(template);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
    //数据回显
    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
        return templateService.findOne(id);
    }
    //修改
    @RequestMapping("/update")
    public Result update(@RequestBody TypeTemplate template){
        try{
            templateService.update(template);
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
            templateService.delete(ids);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }
    //模板的分页查询
    @RequestMapping("/search")
    public PageResult search(@RequestBody TypeTemplate template, Integer page, Integer rows){
        return templateService.findPage(template, page, rows);
    }
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return templateService.selectOptionList();
    }
}
