package com.yy.core.service;

import java.rmi.MarshalledObject;
import java.util.Map;

/**
 * @auther 闫永
 * @date2019/11/21 19:06
 */
public interface SearchService {
    public Map<String,Object> search(Map paramMap);
}
