package com.zhouyuan.rabbit.demo.mapper;

import com.zhouyuan.rabbit.demo.entity.OrderRecordEntity;

import java.util.List;

public interface OrderRecordMapper {
    void insertSelective(OrderRecordEntity entity);
    List<OrderRecordEntity> selectAll();
}
