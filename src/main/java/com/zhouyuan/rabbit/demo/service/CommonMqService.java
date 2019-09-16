package com.zhouyuan.rabbit.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
@Service
public class CommonMqService {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    Environment env;
    private static final Logger log = LoggerFactory.getLogger(CommonMqService.class);

    /**
     * 发送抢单队列消息
     * @param mobile
     */
    public void sendRobMessage(String mobile){
        rabbitTemplate.setExchange(env.getProperty("rabbitmq.rob.product.exchange.name"));
        rabbitTemplate.setRoutingKey(env.getProperty("rabbitmq.rob.product.routingKey.name"));
        try {
            Message message = MessageBuilder.withBody(mobile.getBytes("UTF-8"))
                    .setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
            rabbitTemplate.send(message);
        } catch (UnsupportedEncodingException e) {
            log.error("发送抢单队列消息发生异常，mobile：{}",mobile);
        }
    }
}
