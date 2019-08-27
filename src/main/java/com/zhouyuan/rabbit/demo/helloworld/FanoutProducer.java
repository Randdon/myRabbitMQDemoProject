package com.zhouyuan.rabbit.demo.helloworld;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Fanout模式交换机的消息生产者，不把消息直接发送给队列，而是发送给交换机
 */
public class FanoutProducer {

    private static final String EXCHANGE_NAME = "fanout:exchange:01";

    public static void main(String[] args) {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.253");
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
            String message = "This is a Fanout Broadcast message!";
            channel.basicPublish(EXCHANGE_NAME,"",null,message.getBytes());

            System.out.println("Producer broadcasted a message: [" + message + "]");
            channel.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
