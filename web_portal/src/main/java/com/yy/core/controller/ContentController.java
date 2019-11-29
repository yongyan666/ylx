package com.yy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yy.core.pojo.ad.Content;
import com.yy.core.service.ContentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference
    private ContentService contentService;
  /*  @RequestMapping("/findByCategoryId")
    public List<Content> findByCategoryId(Long categoryId){
        return contentService.findByCategoryId(categoryId);
    }*/
  @RequestMapping("/findByCategoryId")
    public List<Content> findByCategoryId(Long categoryId){
      List<Content> list=contentService.findByCategoryIdFromRedis(categoryId);
      return list;
  }
}
