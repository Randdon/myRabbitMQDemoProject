package com.zhouyuan.rabbit.demo.springeventmodel.event;

import org.springframework.context.ApplicationEvent;

/**
 * Spring事件驱动模型中的事件
 * 相当于-Rabbitmq的message-一串二进制数据流
 */
public class OrderRecordEvent extends ApplicationEvent {

    private String orderNo;
    private String orderType;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public OrderRecordEvent(Object source, String orderNo, String orderType) {
        super(source);
        this.orderNo = orderNo;
        this.orderType = orderType;
    }

    @Override
    public String toString() {
        return "OrderRecordEvent{" +
                "orderNo='" + orderNo + '\'' +
                ", orderType='" + orderType + '\'' +
                '}';
    }
}
