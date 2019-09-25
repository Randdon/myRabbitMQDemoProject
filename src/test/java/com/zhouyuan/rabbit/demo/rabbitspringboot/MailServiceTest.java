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

public class MailServiceTest extends DemoApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(MailServiceTest.class);

    @Autowired
    Environment env;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void mailServiceTest(){
        String address = env.getProperty("mail.to");

        rabbitTemplate.setRoutingKey(env.getProperty("rabbitmq.mail.routingKey.name"));
        rabbitTemplate.setExchange(env.getProperty("rabbitmq.mail.exchange.name"));

        Message message = MessageBuilder.withBody(address.getBytes()).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();

        rabbitTemplate.send(message);

        log.info("####################邮件发送消息完成！##################");
    }
}
