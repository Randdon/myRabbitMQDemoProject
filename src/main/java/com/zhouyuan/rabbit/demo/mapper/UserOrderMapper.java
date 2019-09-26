package com.zhouyuan.rabbit.demo.mapper;

import com.zhouyuan.rabbit.demo.entity.UserOrder;
import org.apache.ibatis.annotations.Param;

public interface UserOrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserOrder record);

    int insertSelective(UserOrder record);

    UserOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserOrder record);

    int updateByPrimaryKey(UserOrder record);

    UserOrder selectByPrimaryKeyAndStatus(@Param("id") Integer id, @Param("status") Integer status);

}