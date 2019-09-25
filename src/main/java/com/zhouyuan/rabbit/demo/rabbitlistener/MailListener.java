package com.zhouyuan.rabbit.demo.rabbitlistener;

import com.zhouyuan.rabbit.demo.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class MailListener {

    private static final Logger log = LoggerFactory.getLogger(MailListener.class);

    @Autowired
    MailService mailService;

    @RabbitListener(queues = "${rabbitmq.mail.queue.name}",containerFactory = "singleListenerContainer")
    public void mailSend(@Payload byte[] message){
        try {
            String address = new String(message,"UTF-8");
            log.info("邮件消息监听服务监听到消息：{}",address);
            mailService.send(address);
        } catch (UnsupportedEncodingException e) {
            log.info("邮件消息监听服务发生异常：{}",e);
        }
    }
}
