package com.zhouyuan.rabbit.demo.rabbitlistener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.zhouyuan.rabbit.demo.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("simpleListener")
public class SimpleListener implements ChannelAwareMessageListener {

    @Autowired
    ObjectMapper objectMapper;

    private static Logger logger = LoggerFactory.getLogger(SimpleListener.class);

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {

        long tag = message.getMessageProperties().getDeliveryTag();

        try {
            byte[] msg = message.getBody();
            Product product = objectMapper.readValue(msg,Product.class);

            //int exception = 1/0;
            logger.info("简单消息监听确认机制监听到消息：{}",product);
            channel.basicAck(tag,true);
        } catch (Exception e) {
            logger.error("简单消息监听确认机制发生异常：{}",e);
            //第二个Boolean参数的意义为是否重入队列
            channel.basicReject(tag,false);
        }

    }
}
