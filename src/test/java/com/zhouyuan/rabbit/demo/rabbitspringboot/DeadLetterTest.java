package com.zhouyuan.rabbit.demo.rabbitspringboot;

import com.zhouyuan.rabbit.demo.DemoApplicationTests;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.io.UnsupportedEncodingException;

public class DeadLetterTest extends DemoApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(DeadLetterTest.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    Environment env;

    @Test
    public void deadLetterTest(){
        rabbitTemplate.setExchange(env.getProperty("rabbitmq.dead.letter.source.exchange.name"));
        rabbitTemplate.setRoutingKey(env.getProperty("rabbitmq.dead.letter.source.routingKey.name"));

        String str = "这是一条发往死信队列的消息！";
        try {
            Message message = MessageBuilder.withBody(str.getBytes("UTF-8"))
                    .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                    .build();

            rabbitTemplate.send(message);
        } catch (UnsupportedEncodingException e) {
            log.error("给死信队列发送消息发生异常：{}",e);
        }
    }
}
