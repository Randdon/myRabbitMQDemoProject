package com.zhouyuan.rabbit.demo.helloworld;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 广播消费者A，将队列1与交换机绑定
 */
public class FanoutConsumerA {

    private static final String EXCHANGE_NAME = "fanout:exchange:01";
    private static final String QUEUE_NAME = "fanout:queue:01";

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.253");
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME,BuiltinExchangeType.FANOUT);
            channel.queueDeclare(QUEUE_NAME,false,false,false,null);
            /**
             * 自己一开始写绑定的时候写成了exchangeBind​方法，该方法与queueBind方法入参一样
             * 结果导致了异常，绑定失败，因为exchangeBind​方法是用来绑定交换机与交换机的，
             * queueBind是用来绑定队列与交换机的
             * queueBind方法的第三个参数用来指定routingkey，不过在Fanout模式下，会忽略routingkey，
             * 即指定不指定都一样，都是广播
             */
            channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"");
            Consumer consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body,"UTF-8");
                    System.out.println("I`m Consumer A, I received the broadcast: [" + message + "]");
                }
            };
            channel.basicConsume(QUEUE_NAME,true,consumer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
