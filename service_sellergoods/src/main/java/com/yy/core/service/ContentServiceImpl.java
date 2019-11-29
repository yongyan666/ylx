package com.yy.core.service;


import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yy.core.dao.ad.ContentDao;
import com.yy.core.entity.PageResult;
import com.yy.core.pojo.ad.Content;
import com.yy.core.pojo.ad.ContentQuery;
import com.yy.core.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@Service
public class ContentServiceImpl implements ContentService{
    @Autowired
    private ContentDao contentDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<Content> findAll() {
        return contentDao.selectByExample(null);
    }

    @Override
    public PageResult findPage(Content content, Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        ContentQuery query = new ContentQuery();
        ContentQuery.Criteria criteria = query.createCriteria();
        if(content!=null){
            if(content.getTitle()!=null&&!"".equals(content.getTitle())){
                criteria.andTitleLike("%"+content.getTitle()+"%");
            }
        }
        Page<Content> contentList =(Page<Content>) contentDao.selectByExample(query);
        return new PageResult(contentList.getTotal(),contentList.getResult());
    }

    @Override
    public void add(Content content) {
        // 1 添加了广告 到mysql数据库
        contentDao.insertSelective(content);
        //2 根据分类id 到redis 中删除对应分类的广告集合
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
    }

    @Override
    public Content findOne(Long id) {
        return contentDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(Content content) {
        // 1 根据广告的id  到mysql 数据库中 查询原来的广告对象
        Content oldContent = contentDao.selectByPrimaryKey(content.getId());
        //2 根据原来广告对象中的分类id 到redis数据库删除对应的广告集合
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(oldContent.getCategoryId());
        //3 根据传入的最新广告分类对象中的id  删除redis中对应的广告数据集合
       redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());

        // 4 将新的广告对象更新到mysql 数据库
        contentDao.updateByPrimaryKeySelective(content);
    }

    @Override
    public void delete(Long[] ids) {
        if(ids!=null){
            for(Long id:ids){
                Content content = contentDao.selectByPrimaryKey(id);
                //根据广告对象中分类id 删除redis 中对应的广告集合数据
                redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
                contentDao.deleteByPrimaryKey(id);
            }
        }
    }

    @Override
    public List<Content> findByCategoryId(Long categoryId) {
        ContentQuery query = new ContentQuery();
        ContentQuery.Criteria criteria = query.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        List<Content> list = contentDao.selectByExample(query);
        return list;
    }

    @Override
    public List<Content> findByCategoryIdFromRedis(Long categoryId) {
        //1   根据分类的id 到redis中取数据
        List<Content> contentList= (List<Content>) redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).get(categoryId);
        //2 如果redis中没有数据 到mysql数据库取
        if(contentList==null){
            //3  如果mysql数据库中 获取到了数据  则存入redis中一份
            contentList=findByCategoryId(categoryId);
            redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).put(categoryId,contentList);

        }
        return contentList;
    }
}
