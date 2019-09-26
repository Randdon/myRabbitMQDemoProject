package com.zhouyuan.rabbit.demo.rabbitlistener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class DeadLetterListener {

    private static final Logger log = LoggerFactory.getLogger(DeadLetterListener.class);

    /**
     * 监听过期消息队列，队列中的消息为死信队列中的未消费的过期消息
     * 如果要测试这个监听方法，则最好用controller调接口的方法测，或者把项目启动起来的同时，再用单元测试来测
     * 不然，直接用单元测试的话，在消息发送到死信队列，再等5秒（设置的ttl）过期，在这个过期消息发送到过期队列之前，
     * 单元测试的主线程就跑完了，那么项目代码也就处于非启动状态了，监听器也就无法进行监听了，之前的邮件和日志等异步调用同样的道理，
     * 但它们之所以能成功，是因为消息直接发到的就是监听的队列，没有死信队列这种二次转发，相当于监听所需的时间更短，
     * 也没有这个死信队列过期消息测试那种强制的过期时间的耗费，所以，在主线程跑完，项目冷却之前就可以监听到消息进入队列，从而分配到一个
     * 活跃的线程，之后，就算主线程跑完，这个监听线程也能将监听任务跑完。
     * @param msg
     */
    @RabbitListener(queues = "${rabbitmq.dead.letter.expired.queue.name}",containerFactory = "singleListenerContainer")
    public void listenToExpiredQueue(@Payload byte[] msg){

        try {
            String message = new String(msg,"UTF-8");
            log.info("监听到死信队列中的过期消息:{}",message);
        } catch (UnsupportedEncodingException e) {
            log.error("监听死信队列中的过期消息出现异常：{}",e);
        }
    }
}
