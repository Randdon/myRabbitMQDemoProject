package com.zhouyuan.rabbit.demo.helloworld;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class DirectConsumerA {
    private static final String QUEUE_NAME = "direct:queue:01";
    private static final String ROUTINGKEY_01 = "direct:routing:01";
    private static final String EXCHANGE_NAME = "direct:exchange:01";
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.253");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME,true,false,false,null);
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,ROUTINGKEY_01);

            Consumer consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body,"UTF-8");
                    System.out.println("This is ConsumerA Listening to Queue01 Binding with Exchange01 by Routing01," +
                            "\r\n" +
                            "This is the received message:[ " + message + " ]");
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
