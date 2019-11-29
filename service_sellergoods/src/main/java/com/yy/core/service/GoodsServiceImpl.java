package com.yy.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yy.core.dao.good.BrandDao;
import com.yy.core.dao.good.GoodsDao;
import com.yy.core.dao.good.GoodsDescDao;
import com.yy.core.dao.item.ItemCatDao;
import com.yy.core.dao.item.ItemDao;
import com.yy.core.dao.seller.SellerDao;
import com.yy.core.entity.GoodsEntity;
import com.yy.core.entity.PageResult;
import com.yy.core.pojo.good.Brand;
import com.yy.core.pojo.good.Goods;
import com.yy.core.pojo.good.GoodsDesc;
import com.yy.core.pojo.good.GoodsQuery;
import com.yy.core.pojo.item.Item;
import com.yy.core.pojo.item.ItemCat;
import com.yy.core.pojo.item.ItemQuery;
import com.yy.core.pojo.seller.Seller;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {
    // 商品的dao
    @Autowired
    private GoodsDao goodsDao;
    // 详情的dao
    @Autowired
    private GoodsDescDao descDao;
    // 库存的dao
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private ItemCatDao catDao;
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private SellerDao sellerDao;
    @Autowired
    private JmsTemplate jmsTemplate;
    //商品上架使用
    @Autowired
    private ActiveMQTopic topicPageAndSolrDestination;
    //为商品的下架使用
    @Autowired
    private ActiveMQQueue queueSolrDeleteDestination;
    @Override
    public void add(GoodsEntity goodsEntity) {
        //1  保存商品对象
        goodsEntity.getGoods().setAuditStatus("0");
        goodsDao.insertSelective(goodsEntity.getGoods());
        //2 保存商品详情对象
        //商品的主键作为商品详情的主键  记得更改dao   resource文件下 GoodsDao.xml
        //<insert id="insertSelective" parameterType="com.yy.core.pojo.good.Goods" useGeneratedKeys="true" keyProperty="id">
        //insert into tb_goods
        goodsEntity.getGoodsDesc().setGoodsId(goodsEntity.getGoods().getId());
        descDao.insertSelective(goodsEntity.getGoodsDesc());
        //3 保存库存集合对象
        insertItm(goodsEntity);
    }




    public void insertItm(GoodsEntity goodsEntity){
        if("1".equals(goodsEntity.getGoods().getIsEnableSpec())){
            // 勾选复选框  有库存数据
            if(goodsEntity.getItemList()!=null){
                // 库存对象
                for(Item item:goodsEntity.getItemList()){
                    // 标题由商品名+规格组成 供消费者搜索使用
                    String title = goodsEntity.getGoods().getGoodsName();
                    String specJsonStr = item.getSpec();
                    // 将json  转成对象
                    Map speMap = JSON.parseObject(specJsonStr, Map.class);
                    // 获取speMap中的value集合
                    Collection<String> values = speMap.values();
                    for(String value:values){
                        // title=title+value   小米手机 5g版本 64g 电信版
                        title+=" "+value;
                    }
                    item.setTitle(title);
                    //  设置库存的对象的属性值
                    setItemValue(goodsEntity,item);
                    itemDao.insertSelective(item);
                }
            }
        }else {
            //  没有勾选   没有库存  但是初始化一条
            Item item = new Item();
            item.setPrice(new BigDecimal("666666666666"));
            // 库存量
            item.setNum(0);
            // 初始化规格
            item.setSpec("{}");
            //标题
            item.setTitle(goodsEntity.getGoods().getGoodsName());
            // 设置库存对象的属性值
            setItemValue(goodsEntity,item);
            itemDao.insertSelective(item);

        }
    }
    private Item setItemValue(GoodsEntity goodsEntity,Item item){
        // 商品的id
        item.setGoodsId(goodsEntity.getGoods().getId());
        //创建时间
        item.setCreateTime(new Date());
        // 更新的时间
        item.setUpdateTime(new Date());
        // 库存的状态
        item.setStatus("0");
        // 分类的id  库存分类
        item.setCategoryid(goodsEntity.getGoods().getCategory3Id());
        // 分类的名称
        ItemCat itemCat = catDao.selectByPrimaryKey(goodsEntity.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        // 品牌的名称
        Brand brand = brandDao.selectByPrimaryKey(goodsEntity.getGoods().getBrandId());
        item.setBrand(brand.getName());
        // 卖家名称
        Seller seller = sellerDao.selectByPrimaryKey(goodsEntity.getGoods().getSellerId());
        item.setSeller(seller.getName());
        // 式例的图片
        String itemImages = goodsEntity.getGoodsDesc().getItemImages();
        List<Map> maps = JSON.parseArray(itemImages, Map.class);
        if(maps!=null&&maps.size()>0){
            String url = String.valueOf(maps.get(0).get("url"));
            item.setImage(url);
        }
        return item;
    }
    // 分页
    @Override
    public PageResult findPage(Goods goods, Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        GoodsQuery query = new GoodsQuery();
        GoodsQuery.Criteria criteria = query.createCriteria();
        criteria.andIsDeleteIsNull();
        if(goods!=null){
            if(goods.getGoodsName()!=null&&!"".equals(goods.getGoodsName())){
                criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
            }
            if(goods.getAuditStatus()!=null&&!"".equals(goods.getAuditStatus())){
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
//            if(goods.getSellerId()!=null && !"".equals(goods.getSellerId())&&!"admin".equals(goods.getSellerId()) && !"labwawa".equals(goods.getSellerId())){
//                criteria.andSellerIdEqualTo(goods.getSellerId());
//            }
        }
        Page<Goods> goodsList =(Page<Goods>) goodsDao.selectByExample(query);
        return new PageResult(goodsList.getTotal(),goodsList.getResult());
    }
    //  数据的回显
    @Override
    public GoodsEntity findOne(Long id) {
        //1 根据商品的id 查询商品对象
        Goods goods = goodsDao.selectByPrimaryKey(id);
        // 2 根据商品的id   查询商品的详情对象
        GoodsDesc goodsDesc = descDao.selectByPrimaryKey(id);
        //3 根据商品的id  查询库存对象
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<Item> items = itemDao.selectByExample(query);
        //4 将查询到的三个对象 封装到 GoodsEntity实体中
        GoodsEntity goodsEntity = new GoodsEntity();
        goodsEntity.setGoods(goods);
        goodsEntity.setGoodsDesc(goodsDesc);
        goodsEntity.setItemList(items);
        return goodsEntity;
    }

    @Override
    public void update(GoodsEntity goodsEntity) {
        // 1修改商品对象
        goodsDao.updateByPrimaryKeySelective(goodsEntity.getGoods());
        // 2 修改商品详情的对象
        descDao.updateByPrimaryKeySelective(goodsEntity.getGoodsDesc());
        // 3 根据库存的id  删除对应的库存集合   根据条件删除
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(goodsEntity.getGoods().getId());itemDao.deleteByExample(query);
        // 4 添加库存集合数据
        insertItm(goodsEntity);
    }
    // 删除     ？      404            物理删除就是普通的删除
    // 逻辑删除本质上是修改
    @Override
    public void delete(final Long id) {

        Goods goods = new Goods();
        goods.setId(id);
        goods.setIsDelete("1");

        goodsDao.updateByPrimaryKeySelective(goods);//除了刚才设置两个属性外  读取原来字段值
        jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                return textMessage;
            }
        });

    }
    // 修改状态
    @Override
    public void updateStatus(final Long id, String status) {

        // 1 根据商品的id  修改商品的状态码
        Goods goods = new Goods();
        goods.setId(id);
        goods.setAuditStatus(status);
        goodsDao.updateByPrimaryKeySelective(goods);
        // 2 根据商品id   修改库存对象的状态码
        Item item = new Item();
        item.setStatus(status);
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        itemDao.updateByExampleSelective(item,query);
        //将商品的id 作为消息发送给消息服务器
        if ("1".equals(status)){
            jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                    return textMessage;
                    //接受方有两个  一个是search 一个是page
                }
            });
        }
    }
}
