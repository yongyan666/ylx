package cn.yy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yy.core.entity.Result;
import com.yy.core.pojo.user.User;
import com.yy.core.service.UserService;
import com.yy.core.util.PhoneFormatCheckUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @auther 闫永
 * @date2019/11/28 19:41
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Reference
    private UserService userService;
    //发送短信验证码 手机号
    @RequestMapping("/sendCode")
    public Result sendCode(String phone){
        try{

            if (phone==null||"".equals(phone)){
                return new Result(false,"手机号不能为空");
            }
            if(!PhoneFormatCheckUtils.isPhoneLegal(phone)){
                return new Result(false,"手机号格式不对");
            }
            userService.sendCode(phone);
            return new Result(true,"发送成功");
        }catch (Exception e){
            return new Result(false,"发送失败");
        }
    }
    @RequestMapping("/add")
    public Result add(@RequestBody User user, String smscode){
        try{
            Boolean isCheck=userService.checkSmsCode(user.getPhone(),smscode);
            if (!isCheck){
                return new Result(false,"手机或者验证码不正确");
            }
            user.setSourceType("1");
            user.setStatus("Y");
            user.setCreated(new Date());
            user.setUpdated(new Date());
            userService.add(user);
            return new Result(true,"用户注册成功");
        }catch (Exception e){
            return new Result(false,"用户注册失败");
        }
    }
}
