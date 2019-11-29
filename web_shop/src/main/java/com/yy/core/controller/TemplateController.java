package com.yy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yy.core.pojo.template.TypeTemplate;
import com.yy.core.service.TemplateService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TemplateController {
    @Reference
    private TemplateService templateService;


    // 修改  数据回显
    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
        return templateService.findOne(id);

    }
    // 根据模板id  查询规格的集合 和规格选项集合
    /**
     * [{"id":27,"text":"网络","options":["{id:1,options_name:3g}"]},{"id":32,"text":"机身内存",options：["id:3",option_name:128G]}]
     * */
    @RequestMapping("/findBySpecList")
    public List<Map> findBySpecList(Long id){
        List<Map> list = templateService.findBySpecList(id);
        return list;
    }

}
