# myRabbitMQDemoProject
Learn RabbitMQ from a Lesson.

一些总结：

#
com.zhouyuan.rabbit.demo.mapper.UserOrderMapper.insertSelective方法里加上

`useGeneratedKeys="true" keyProperty="id"`

这两个参数以后就可以在入库后获得新插入的那行数据的主键id，
这个返回的主键id会在插入的数据库实体里。获取方式如下所示：
`Integer id=userOrder.getId();`

还可以这样写来获取入库后的数据的主键：

    <insert id="addDonate" parameterType="com.wangchen.drplant.model.Donate">
        <selectKey resultType="java.lang.Integer" order="AFTER" keyProperty="id">
            SELECT LAST_INSERT_ID() AS id
        </selectKey>
        insert into donate (
        member_id,
        open_id,
        year,
        month,
        day,
        step_count
        )
        values (
        #{memberId},
        #{openId},
        #{year},
        #{month},
        #{day},
        #{stepCount}
        )
    </insert>
    
同样，获取到的主键id会在入参donate里
