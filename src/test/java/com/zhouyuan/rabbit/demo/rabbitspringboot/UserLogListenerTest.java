package com.zhouyuan.rabbit.demo.rabbitspringboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhouyuan.rabbit.demo.DemoApplicationTests;
import com.zhouyuan.rabbit.demo.entity.User;
import com.zhouyuan.rabbit.demo.entity.UserLog;
import com.zhouyuan.rabbit.demo.mapper.UserLogMapper;
import com.zhouyuan.rabbit.demo.mapper.UserMapper;
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

public class UserLogListenerTest extends DemoApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(UserLogListenerTest.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserLogMapper userLogMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Environment env;

    @Test
    public void userLogListenerTest(){
        login("randdon","123789654");
    }

    public void login(String username,String password){

        User user = userMapper.selectByUserNamePassword(username,password);
        try {
            UserLog userLog = new UserLog(username,"Login","login",objectMapper.writeValueAsString(user));

            /**
             * 这样写就是同步写法，有可能会十分耗时而使得主线程无法往下进行
             */
            //userLogMapper.insertSelective(userLog);

            /**
             * 采用rabbitmq异步写用户操作日志
             */
            rabbitTemplate.setExchange(env.getProperty("rabbitmq.log.user.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("rabbitmq.log.user.routingKey.name"));
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

            Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(userLog))
                    .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                    .build();

            rabbitTemplate.convertAndSend(message);

            //TODO：塞权限数据-资源数据-视野数据

        } catch (Exception e) {
            log.error("记录用户登录操作日志时发生异常：{}",e);
        }
    }
}
