package com.yy.page.listener;

import com.yy.core.service.CmsService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Map;

/**
 * @auther 闫永
 * @date2019/11/27 19:17
 */
public class PageListener implements MessageListener {
    @Autowired
    private CmsService cmsService;
    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage atm = (ActiveMQTextMessage) message;
        try{
            String goodsId = atm.getText();
            Map<String, Object> goodsData = cmsService.findGoodsData(Long.parseLong(goodsId));
            cmsService.createStaticPage(Long.parseLong(goodsId),goodsData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
