package com.zhouyuan.rabbit.demo.rabbitlistener;

import com.zhouyuan.rabbit.demo.service.ConcurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
@Component
public class RobListener {
    @Autowired
    ConcurrencyService concurrencyService;
    private static Logger log = LoggerFactory.getLogger(RobListener.class);
    @RabbitListener(queues = "${rabbitmq.rob.product.queue.name}",
            containerFactory = "singleListenerContainer")
    public void consumeMessage(@Payload byte[] message){
        try {
            String mobile = new String(message,"UTF-8");
            concurrencyService.manageRobbing(mobile);
            log.info("接收到抢单消息，mobile：{}",mobile);
        } catch (UnsupportedEncodingException e) {
            log.error("接受抢单消息出现异常",e);
        }
    }
}
