package com.yy.core.dao.item;

import com.yy.core.pojo.item.ItemCat;
import com.yy.core.pojo.item.ItemCatQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemCatDao {
    int countByExample(ItemCatQuery example);

    int deleteByExample(ItemCatQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(ItemCat record);

    int insertSelective(ItemCat record);

    List<ItemCat> selectByExample(ItemCatQuery example);

    ItemCat selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ItemCat record, @Param("example") ItemCatQuery example);

    int updateByExample(@Param("record") ItemCat record, @Param("example") ItemCatQuery example);

    int updateByPrimaryKeySelective(ItemCat record);

    int updateByPrimaryKey(ItemCat record);
}