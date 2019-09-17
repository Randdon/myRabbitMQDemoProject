package com.zhouyuan.rabbit.demo.helloworld;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class DirectConsumerB {
    private static final String QUEUE_NAME = "direct:queue:02";
    private static final String ROUTINGKEY_02 = "direct:routing:02";
    private static final String ROUTINGKEY_03 = "direct:routing:03";
    private static final String EXCHANGE_NAME = "direct:exchange:01";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.253");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTINGKEY_02);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTINGKEY_03);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("This is ConsumerB Listening to Queue02 Binding with Exchange01 by Routing02 and Routing03," +
                            "\r\n" +
                            "This is the received message:[ " + message + " ]");
                }
            };

            channel.basicConsume(QUEUE_NAME, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
}
