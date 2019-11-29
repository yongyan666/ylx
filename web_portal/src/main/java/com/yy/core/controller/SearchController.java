package com.yy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yy.core.service.SearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @auther 闫永
 * @date2019/11/21 20:39
 */
@RestController
@RequestMapping("/itemsearch")
public class SearchController {
    @Reference
    private SearchService searchService;
    @RequestMapping("/search")
    public Map<String,Object> search(@RequestBody Map paramMap){
        Map<String, Object> resultMap = searchService.search(paramMap);
        return resultMap;
    }
}
