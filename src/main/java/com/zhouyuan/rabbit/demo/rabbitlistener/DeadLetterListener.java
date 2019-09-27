package com.zhouyuan.rabbit.demo.rabbitlistener;

import com.zhouyuan.rabbit.demo.entity.UserOrder;
import com.zhouyuan.rabbit.demo.mapper.UserOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@Component
public class DeadLetterListener {

    private static final Logger log = LoggerFactory.getLogger(DeadLetterListener.class);

    @Autowired
    UserOrderMapper userOrderMapper;
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

    /**
     * 用户下单消息死信队列的支付超时过期消息队列的监听器
     * @param id
     */
    @RabbitListener(queues = "${rabbitmq.user.order.dynamic.dead.letter.expired.queue.name}",containerFactory = "multiLisenerContainer")
    public void listenToUserOrderExpiredQueue(@Payload Integer id){

        log.info("用户下单消息死信队列的支付超时过期消息队列的监听器监听到消息：{}",id);

        UserOrder userOrder = userOrderMapper.selectByPrimaryKeyAndStatus(id,1);
        if (null != userOrder){
            //超时未支付，设置状态为取消订单
            userOrder.setStatus(3);
            userOrder.setUpdateTime(new Date());
            userOrderMapper.updateByPrimaryKey(userOrder);
        }
        /**
         * 如果支付成功不在这里做处理，有单独的支付接口处理支付业务，那么支付成功的消息到了这个消费者的时候不会对这个消息做处理，
         * 但是在开启自动确认的情况下，消息到了消费者就是被确认消费了，会在队列中删除掉，不会有垃圾消息堆积的情况
         */
    }

    /**
     * 用户下单消息死信队列的支付超时过期消息队列的监听器-动态配置TTL
     * @param id
     */
    @RabbitListener(queues = "${rabbitmq.user.order.dead.letter.expired.queue.name}",containerFactory = "multiLisenerContainer")
    public void listenToUserOrderDynamicTTLExpiredQueue(@Payload Integer id){

        log.info("用户下单消息死信队列的支付超时过期消息队列的监听器监听到消息：{}",id);

        UserOrder userOrder = userOrderMapper.selectByPrimaryKeyAndStatus(id,1);
        if (null != userOrder){
            //超时未支付，设置状态为取消订单
            userOrder.setStatus(3);
            userOrder.setUpdateTime(new Date());
            userOrderMapper.updateByPrimaryKey(userOrder);
        }
    }


    /**
     * 用户下单消息死信队列监听器
     * 只要监听了，就不会进过期队列，从而引发一个问题，即如果我支付没超时，那么怎么防止这个消息进到过期消息队列里去
     * 支付没超时的业务逻辑是怎么样的？？？？？？？？？？？？？？？
     */
/*    @RabbitListener(queues = "${rabbitmq.user.order.dead.letter.queue.name}",containerFactory = "multiLisenerContainer")
    public void listenToUserOrderDeadLetterQueue(@Payload Integer id){
        try {
            log.info("用户下单死信队列监听器监听到消息：{}",id);
            Thread.sleep(20000);
            log.info("用户下单死信队列监听器线程休眠2秒后执行此语句！");
        } catch (InterruptedException e) {
            log.error("用户下单死信队列监听器监听到消息：{}",e);
        }
    }*/
}
