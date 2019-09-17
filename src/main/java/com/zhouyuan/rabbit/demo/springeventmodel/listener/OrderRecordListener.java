package com.zhouyuan.rabbit.demo.springeventmodel.listener;

import com.zhouyuan.rabbit.demo.entity.OrderRecordEntity;
import com.zhouyuan.rabbit.demo.mapper.OrderRecordMapper;
import com.zhouyuan.rabbit.demo.springeventmodel.event.OrderRecordEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 这就是Spring事件驱动模型的监听器-跟RabbitMQ的Listener几乎是一个理念
 * 如果不加@Component注解，发布者发布事件之后发布者获取不到，不会进onApplicationEvent方法
 */
@Component
public class OrderRecordListener implements ApplicationListener<OrderRecordEvent> {
    @Autowired
    OrderRecordMapper orderRecordMapper;

    @Override
    public void onApplicationEvent(OrderRecordEvent orderRecordEvent) {

        OrderRecordEntity entity = new OrderRecordEntity();

        BeanUtils.copyProperties(orderRecordEvent, entity);

        orderRecordMapper.insertSelective(entity);
    }
}
