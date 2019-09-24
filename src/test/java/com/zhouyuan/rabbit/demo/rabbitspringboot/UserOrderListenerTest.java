package com.zhouyuan.rabbit.demo.rabbitspringboot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhouyuan.rabbit.demo.DemoApplicationTests;
import com.zhouyuan.rabbit.demo.dto.LogDto;
import com.zhouyuan.rabbit.demo.dto.UserOrderDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
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

        log.debug("接收到数据： {} ",dto);

        /**
         * 用户下单记录-入库
         */

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


        /**
         * 系统级别-日志记录-异步分出去
         */
        LogDto logDto = new LogDto("userOrderListenerTest",dto.toString());

        rabbitTemplate.setExchange(env.getProperty("rabbitmq.log.system.exchange.name"));
        rabbitTemplate.setRoutingKey(env.getProperty("rabbitmq.log.system.routingKey.name"));
        /**
         * 直接发送对象数据的方法，不用字节流的方式发送
         */
        rabbitTemplate.convertAndSend(logDto, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                MessageProperties properties = message.getMessageProperties();
                properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                properties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME,LogDto.class);
                return message;
            }
        });

        /**
         * 还有很多业务逻辑...
         */
        log.info("主线程还是照样坦荡荡的往前走.....");

    }
}
