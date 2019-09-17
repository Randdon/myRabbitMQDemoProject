package com.zhouyuan.rabbit.demo.helloworld;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class DirectProducer {

    private static final String EXCHANGE_NAME = "direct:exchange:01";
    private static final String ROUTINGKEY_01 = "direct:routing:01";
    private static final String ROUTINGKEY_02 = "direct:routing:02";
    private static final String ROUTINGKEY_03 = "direct:routing:03";

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.253");
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            String message1 = "This is routing01`s message!";
            String message2 = "This is routing02`s message!";
            String message3 = "This is routing03`s message!";
            channel.basicPublish(EXCHANGE_NAME, ROUTINGKEY_01, null, message1.getBytes());
            channel.basicPublish(EXCHANGE_NAME, ROUTINGKEY_02, null, message2.getBytes());
            channel.basicPublish(EXCHANGE_NAME, ROUTINGKEY_03, null, message3.getBytes());
            System.out.println("DirectProducer finished publishing three messages.");
            channel.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
}
