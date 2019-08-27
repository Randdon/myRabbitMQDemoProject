package com.zhouyuan.rabbit.demo.helloworld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TopicProducer {

    private static final String EXCHANGE_NAME = "topic:exchange:01";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.253");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            String message1 = "This a Topic Exchange Message!,my routingkey is zhouyuan.revolution.translation";
            String message2 = "This a Topic Exchange Message!,my routingkey is zhouyuan.revolution.translation.irn";
            String message3 = "This a Topic Exchange Message!,my routingkey is zhouyuan.revolution";

            channel.basicPublish(EXCHANGE_NAME,"zhouyuan.revolution.translation",null,message1.getBytes());
            channel.basicPublish(EXCHANGE_NAME,"zhouyuan.revolution.translation.irn",null,message2.getBytes());
            channel.basicPublish(EXCHANGE_NAME,"zhouyuan.revolution",null,message3.getBytes());

            System.out.println("Topic Producer finished sending message!");
            channel.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
