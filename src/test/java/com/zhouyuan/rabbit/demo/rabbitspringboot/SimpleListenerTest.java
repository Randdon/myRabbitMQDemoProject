package com.zhouyuan.rabbit.demo.rabbitspringboot;

import com.zhouyuan.rabbit.demo.DemoApplicationTests;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class SimpleListenerTest extends DemoApplicationTests {

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    Environment env;

    @Test
    public void simpleListenerTest(){
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setExchange(env.getProperty("rabbitmq.simple.container.routingKey.name"));
    }
}
