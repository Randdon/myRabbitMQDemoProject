package com.zhouyuan.rabbit.demo.rabbitspringboot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhouyuan.rabbit.demo.DemoApplicationTests;
import com.zhouyuan.rabbit.demo.dto.UserOrderDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class UserOrderListenerTest extends DemoApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(UserOrderListenerTest.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Environment env;

    @Test
    public void userOrderListenerTest(){
        UserOrderDto dto = new UserOrderDto();
        dto.setOrderNo("10010");
        dto.setUserId(1);

        rabbitTemplate.setExchange(env.getProperty("rabbitmq.user.order.exchange.name"));
        rabbitTemplate.setRoutingKey(env.getProperty("rabbitmq.user.order.routingKey.name"));
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        try {
            Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(dto))
                    .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                    .build();
            rabbitTemplate.convertAndSend(message);
        } catch (JsonProcessingException e) {
            log.error("用户商城下单发送消息发生异常：{}",e);
        }
    }
}
