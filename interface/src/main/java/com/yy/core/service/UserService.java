package com.yy.core.service;

import com.yy.core.pojo.user.User;

/**
 * @auther 闫永
 * @date2019/11/28 19:54
 */
public interface UserService {
    //发送验证码
    public void sendCode(String phone);
    //校验
    public  Boolean checkSmsCode(String phone,String smsCode);
    //注册
    public void add(User user);

}
