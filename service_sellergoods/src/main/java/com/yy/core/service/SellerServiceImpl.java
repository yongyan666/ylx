package com.yy.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yy.core.dao.seller.SellerDao;
import com.yy.core.entity.PageResult;
import com.yy.core.pojo.seller.Seller;
import com.yy.core.pojo.seller.SellerQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class SellerServiceImpl implements SellerService{
    @Autowired
    private SellerDao sellerDao;
    @Override
    public void add(Seller seller) {
        // 手动初始化时间
        seller.setCreateTime(new Date());
        //审核状态   默认都是0
        seller.setStatus("0");
        sellerDao.insertSelective(seller);
    }

    @Override
    public void updateStatus(String sellerId, String status) {
        Seller seller = sellerDao.selectByPrimaryKey(sellerId);
        seller.setStatus(status);
        seller.setSellerId(sellerId);// 两个值  其它的为空
        sellerDao.updateByPrimaryKey(seller);//不修改其它字段的值
    }

    @Override
    public PageResult findPage(Seller seller, Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        // 根据条件查       封装条件
        SellerQuery query = new SellerQuery();
        SellerQuery.Criteria criteria=query.createCriteria();
        if (seller!=null) {
            if (seller.getStatus() != null && !"".equals(seller.getStatus())) {
                criteria.andStatusEqualTo(seller.getStatus());
            }
            // 店铺名称
            if (seller.getName() != null && !"".equals(seller.getName())) {
                criteria.andNameLike("%" + seller.getName() + "%");
            }
            // 公司名称
            if (seller.getNickName() != null && !"".equals(seller.getNickName())) {
                criteria.andNickNameLike("%" + seller.getNickName() + "%");
            }
        }
        Page<Seller> sellers =(Page<Seller>) sellerDao.selectByExample(query);
        return new PageResult(sellers.getTotal(),sellers.getResult());
    }

    @Override
    public Seller findOne(String id) {
        return sellerDao.selectByPrimaryKey(id);
    }
}
