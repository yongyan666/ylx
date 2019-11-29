package com.yy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yy.core.entity.PageResult;
import com.yy.core.entity.Result;
import com.yy.core.pojo.ad.Content;
import com.yy.core.service.ContentService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference
    private ContentService contentService;
    @RequestMapping("/findAll")
    public List<Content> findaAll(){
        List<Content> list = contentService.findAll();
        return list;
    }

    @RequestMapping("/add")
    public Result add(@RequestBody Content content){
        try{
            contentService.add(content);
            return new Result(true,"保存成功");
        }catch (Exception e){
            return new Result(false,"保存失败");
        }

    }
    @RequestMapping("/findOne")
    public Content findOne(Long id){

        Content one = contentService.findOne(id);
        return one;
    }

    @RequestMapping("update")
    public Result update(@RequestBody Content content){
        try{
            contentService.update(content);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }


    // 删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            contentService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }

    }
    // 分页查询
    @RequestMapping("/search")
    public PageResult search(@RequestBody Content content, Integer page, Integer rows  ){
        PageResult result =contentService.findPage(content, page, rows);
        return result;
    }

}
