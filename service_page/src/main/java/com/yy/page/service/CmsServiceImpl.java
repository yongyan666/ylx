package com.yy.page.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.yy.core.dao.good.GoodsDao;
import com.yy.core.dao.good.GoodsDescDao;
import com.yy.core.dao.item.ItemCatDao;
import com.yy.core.dao.item.ItemDao;
import com.yy.core.pojo.good.Goods;
import com.yy.core.pojo.good.GoodsDesc;
import com.yy.core.pojo.item.Item;
import com.yy.core.pojo.item.ItemCat;
import com.yy.core.pojo.item.ItemQuery;
import com.yy.core.service.CmsService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther 闫永
 * @date2019/11/26 14:15
 */
@Service
public class CmsServiceImpl implements CmsService, ServletContextAware {
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private GoodsDescDao descDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private ItemCatDao catDao;
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Autowired
    private ServletContext servletContext;
    @Override
    public Map<String, Object> findGoodsData(Long goodsId) {
        HashMap<String,Object> resultMap=new HashMap<>();
        //1.获取商品的数据
        Goods goods = goodsDao.selectByPrimaryKey(goodsId);
        //2获取商品的详情数据
        GoodsDesc goodsDesc = descDao.selectByPrimaryKey(goodsId);
        //3获取库存集合的数据
        ItemQuery query=new ItemQuery();
        ItemQuery.Criteria criteria =query.createCriteria();
        criteria.andGoodsIdEqualTo(goodsId);
        List<Item> itemList = itemDao.selectByExample(query);
        //4.获取商品对应的分类数据
        if (goods!=null){
            ItemCat itemCat1 = catDao.selectByPrimaryKey(goods.getCategory1Id());
            ItemCat itemCat2 = catDao.selectByPrimaryKey(goods.getCategory2Id());
            ItemCat itemCat3 = catDao.selectByPrimaryKey(goods.getCategory3Id());

            //封装数据
            resultMap.put("itemCat1",itemCat1.getName());
            resultMap.put("itemCat2",itemCat2.getName());
            resultMap.put("itemCat3",itemCat3.getName());

        }
        //5将商品的所有数据封装成map 返回 key---->需要看模板
        resultMap.put("goods",goods);
        resultMap.put("goodsDesc",goodsDesc);
        resultMap.put("itemList",itemList);
        return resultMap;
    }

    @Override
    public void createStaticPage(Long goodsId, Map<String, Object> rootMap) throws Exception {
        //1.获取模板的初始化对象
        Configuration configuration = freeMarkerConfig.getConfiguration();
        //2.获取模板对象
        Template template = configuration.getTemplate("item.ftl");
        //3.创建输出流 指定生成静态页面的位置和名称
        String path=goodsId+".html";
        System.out.println("====path===="+path);
        //获取绝对途径
        String realPath = getRealPath(path);
        Writer out = new OutputStreamWriter(new FileOutputStream(new File(realPath)), "utf-8");
        //4生成
        template.process(rootMap,out);
        //关闭流
        out.close();
    }
    //将相对路径转换成绝对路径
    private  String getRealPath(String path){
        String realPath = servletContext.getRealPath(path);
        System.out.println("====realPath===="+realPath);
        return realPath;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {

    }
}
