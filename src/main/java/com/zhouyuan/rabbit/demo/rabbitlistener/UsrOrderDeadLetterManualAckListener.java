package com.zhouyuan.rabbit.demo.rabbitlistener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.zhouyuan.rabbit.demo.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsrOrderDeadLetterManualAckListener implements ChannelAwareMessageListener {

    @Autowired
    ObjectMapper objectMapper;

    private static Logger log = LoggerFactory.getLogger(UsrOrderDeadLetterManualAckListener.class);

    /**
     * 即使不去ack，也不会使死信队列中的消息过期从而转发到过期队列中去
     * @param message
     * @param channel
     * @throws Exception
     */
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {

        long tag = message.getMessageProperties().getDeliveryTag();

        try {
            byte[] msg = message.getBody();
            Integer id = objectMapper.readValue(msg,Integer.class);

            log.info("用户下单死信队列监听器监听到消息：{}",id);
            Thread.sleep(20000);
            log.info("用户下单死信队列监听器线程休眠2秒后执行此语句！此时还没有ack");
            channel.basicAck(tag,true);

        } catch (InterruptedException e) {
            log.error("用户下单死信队列监听器监听到消息：{}",e);
            channel.basicReject(tag,false);

        }
    }
}
