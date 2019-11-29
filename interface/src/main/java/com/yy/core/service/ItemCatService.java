package com.yy.core.service;


import com.yy.core.pojo.item.ItemCat;

import java.util.List;

public interface ItemCatService {
    public List<ItemCat> findByParentId(Long parentId);
    public ItemCat findOne(Long id);
    public void update(ItemCat itemCat);
    public void delete(Long[] ids);

    public List<ItemCat> findAll();
}
