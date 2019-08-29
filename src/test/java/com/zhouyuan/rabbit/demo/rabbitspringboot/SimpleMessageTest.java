package com.zhouyuan.rabbit.demo.rabbitspringboot;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhouyuan.rabbit.demo.DemoApplicationTests;
import com.zhouyuan.rabbit.demo.entity.OrderRecordEntity;
import org.junit.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.Payload;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public class SimpleMessageTest extends DemoApplicationTests {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    Environment environment;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    /**
     * 在com.zhouyuan.rabbit.demo.config.RabbitMqConfig里配置basicExchange\basicQueue\basicBinding这三个
     * 基本消息模型，就可以跑通本测试方法，在RabbitMQ控制后台看到队列和交换机以及发送的消息
     */
    public void sendSimpleMessage(){
        rabbitTemplate.setExchange(environment.getProperty("rabbitmq.simple.message.exchange.name"));
        rabbitTemplate.setRoutingKey(environment.getProperty("rabbitmq.simple.message.routingKey.name"));
        String content = "This is first Spring boot with RabbitMQ test!";
        try {
            Message message = MessageBuilder.withBody(content.getBytes("UTF-8")).build();
            rabbitTemplate.send(message);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * 发送对象消息
     */
    public void sendObjectMessage(){
        rabbitTemplate.setExchange(environment.getProperty("rabbitmq.simple.message.exchange.name"));
        rabbitTemplate.setRoutingKey(environment.getProperty("rabbitmq.simple.message.routingKey.name"));
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        OrderRecordEntity orderRecordEntity = new OrderRecordEntity(1,"2","cat",new Date(),new Date());
        try {
            Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(orderRecordEntity))
                    .setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
            rabbitTemplate.convertAndSend(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * RabbitMQ消息监听方法
     * ${rabbitmq.simple.message.queue.name}取自application.properties:43，
     * singleListenerContainer取自com/zhouyuan/rabbit/demo/config/RabbitMqConfig.java:36
     */
    @RabbitListener(queues = "${rabbitmq.simple.message.queue.name}",containerFactory = "singleListenerContainer")
    public void rabbitListener(@Payload byte[] message){
        try {
            //接收字符串
            //String result = new String(message,"UTF-8");
            OrderRecordEntity result = objectMapper.readValue(message,OrderRecordEntity.class);
            System.out.println("接收到的消息为：【" + result + "】");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
