package com.yy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yy.core.entity.PageResult;
import com.yy.core.entity.Result;
import com.yy.core.pojo.ad.Content;
import com.yy.core.pojo.ad.ContentCategory;
import com.yy.core.service.CategoryService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contentCategory")
public class CategoryController {
    @Reference
    private CategoryService categoryService;
    @RequestMapping("/findAll")
    public List<ContentCategory> findAll(){
        return categoryService.findAll();
    }
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page, Integer rows){
        return categoryService.findPage(page,rows);
    }
    @RequestMapping("/add")
    public Result add(@RequestBody ContentCategory category){
        try{
            categoryService.add(category);
            return new Result(true,"成功");
        }catch (Exception e){
            return new Result(false,"失败");
        }
    }
    @RequestMapping("/findOne")
    public ContentCategory findOne(Long id){
        return categoryService.findOne(id);
    }
    @RequestMapping("/update")
    public Result update(ContentCategory category){
        try{
            categoryService.update(category);
            return new Result(true,"成功");
        }catch (Exception e){
            return new Result(false,"失败");
        }
    }
    @RequestMapping("/delete")
    public void delete(Long[] ids){
        categoryService.delete(ids);
    }
    @RequestMapping("/search")
    public PageResult search(@RequestBody ContentCategory category, Integer page, Integer rows  ){
        return categoryService.findPage(category, page, rows);
    }

}
