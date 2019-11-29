package com.yy.core.listener;

import com.yy.core.service.SolrManagerService;
import org.apache.activemq.command.ActiveMQBlobMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * @auther 闫永
 * @date2019/11/27 16:14
 */
public class ItemSearchListener implements MessageListener {
    @Autowired
    private SolrManagerService solrManagerService;
    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage atm = (ActiveMQTextMessage) message;
        try{
            String goodId = atm.getText();
            solrManagerService.saveItemToSolr(Long.parseLong(goodId));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
