package com.zhouyuan.rabbit.demo.helloworld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class SendHello {

    private static final String QUEUE_NAME = "ZhouYuan:Rabbit:Test";
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.253");
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME,false,false,false,null);
            String message = "ZhouYuan`s first RabbitMQ HelloWorld!";
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
            System.out.println("ZhouYuan Sent: '" + message + " '");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
