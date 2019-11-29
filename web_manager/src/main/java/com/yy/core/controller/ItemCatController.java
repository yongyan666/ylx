package com.yy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yy.core.pojo.item.ItemCat;
import com.yy.core.service.ItemCatService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;

    @RequestMapping("/findByParentId")
    public List<ItemCat> findByParentId(Long parentId){
        List<ItemCat> list = itemCatService.findByParentId(parentId);
        return list;
    }
    @RequestMapping("/update")
    public void update(ItemCat itemCat){
        itemCatService.update(itemCat);
    }
    @RequestMapping("/delete")
    public void delete(Long[] ids){
        itemCatService.delete(ids);
    }
    @RequestMapping("/findone")
    public ItemCat findone(Long id){
        return itemCatService.findOne(id);
    }
    @RequestMapping("/findAll")
    public List<ItemCat> findAll(){
        return itemCatService.findAll();
    }
}
