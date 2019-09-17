package com.zhouyuan.rabbit.demo.springeventmodel;

import com.zhouyuan.rabbit.demo.DemoApplicationTests;
import com.zhouyuan.rabbit.demo.entity.OrderRecordEntity;
import com.zhouyuan.rabbit.demo.mapper.OrderRecordMapper;
import com.zhouyuan.rabbit.demo.springeventmodel.event.OrderRecordEvent;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

public class OrderRecordTest extends DemoApplicationTests {

    @Autowired
    //Spring事件驱动模型的发布者，类似于RabbitTemplate
            ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    OrderRecordMapper mapper;

    @Test
    public void pushOrderRecordTest() {
        OrderRecordEvent orderRecordEvent = new OrderRecordEvent(this, "ydTest_201908271111", "红米Note7");
        //事件发布后监听者会监听到该条事件，并根据代码逻辑插入到数据库里
        applicationEventPublisher.publishEvent(orderRecordEvent);
    }

    @Test
    public void mybatisTest() {
        List<OrderRecordEntity> entities = mapper.selectAll();
        System.out.println(entities.size());
    }
}
