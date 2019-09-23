package com.zhouyuan.rabbit.demo.rabbitspringboot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhouyuan.rabbit.demo.DemoApplicationTests;
import com.zhouyuan.rabbit.demo.entity.Product;
import org.junit.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * 简单消息监听确认机制测试类
 */
public class SimpleListenerTest extends DemoApplicationTests {

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    Environment env;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void simpleListenerTest(){
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setExchange(env.getProperty("rabbitmq.simple.container.exchange.name"));
        rabbitTemplate.setRoutingKey(env.getProperty("rabbitmq.simple.container.routingKey.name"));

        Product product = new Product(1,"simpleListenerTest",20);
        try {
            Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(product))
                    .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                    .build();
            rabbitTemplate.convertAndSend(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
