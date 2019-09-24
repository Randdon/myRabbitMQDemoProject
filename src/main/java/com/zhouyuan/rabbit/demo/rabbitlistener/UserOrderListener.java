package com.zhouyuan.rabbit.demo.rabbitlistener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.zhouyuan.rabbit.demo.dto.UserOrderDto;
import com.zhouyuan.rabbit.demo.entity.UserOrder;
import com.zhouyuan.rabbit.demo.mapper.UserOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("userOrderListener")
public class UserOrderListener implements ChannelAwareMessageListener {

    private static final Logger log = LoggerFactory.getLogger(UserOrderListener.class);

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserOrderMapper userOrderMapper;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            byte[] body = message.getBody();
            UserOrderDto dto = objectMapper.readValue(body, UserOrderDto.class);
            log.info("用户商城下单监听到消息：{}",dto);
            UserOrder userOrder = new UserOrder();
            BeanUtils.copyProperties(dto,userOrder);
            userOrder.setStatus(1);
            userOrderMapper.insertSelective(userOrder);
            channel.basicAck(tag,false);
        } catch (Exception e) {
            log.error("用户商城下单发生异常：{}",e);
            channel.basicReject(tag,false);
        }
    }
}
