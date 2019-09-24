package com.zhouyuan.rabbit.demo.rabbitlistener;

import com.zhouyuan.rabbit.demo.dto.LogDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component("logSystemListener")
public class LogSystemListener {

    private static final Logger log = LoggerFactory.getLogger(LogSystemListener.class);

    @RabbitListener(queues = "${rabbitmq.log.system.queue.name}",containerFactory = "multiLisenerContainer")
    public void logSystemConsume(@Payload LogDto logDto){
        try {
            log.info("系统日志监听器监听到消息：{}",logDto);
        } catch (Exception e) {
            log.error("系统日志监听器监听消息发生异常：{}",e);
        }
    }
}
