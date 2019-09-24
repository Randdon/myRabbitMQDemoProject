package com.zhouyuan.rabbit.demo.rabbitlistener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhouyuan.rabbit.demo.entity.UserLog;
import com.zhouyuan.rabbit.demo.mapper.UserLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 用户操作日志监听器
 * @Component注解必须加，否则监听不到消息
 */
@Component
public class LogUserListener {

    private static final Logger log = LoggerFactory.getLogger(LogUserListener.class);

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserLogMapper userLogMapper;

    @RabbitListener(queues = "${rabbitmq.log.user.queue.name}",containerFactory = "singleListenerContainer")
    public void logUserOperationRecord(@Payload byte[] message){

        try {
            UserLog userLog = objectMapper.readValue(message,UserLog.class);
            userLogMapper.insertSelective(userLog);
            log.info("用户操作日志监听器监听到消息：{}",userLog);
        } catch (Exception e) {
            log.error("用户操作日志监听器监听过程中发生异常：{}",e);
        }
    }
}
