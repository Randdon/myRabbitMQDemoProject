package com.zhouyuan.rabbit.demo.helloworld;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TopicConsumerB {
    private static final String EXCHANGE_NAME = "topic:exchange:01";
    private static final String QUEUE_NAME = "topic:queue:02";
    private static final String ROUTINGKEY = "zhouyuan.revolution.#";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.253");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
            channel.queueDeclare(QUEUE_NAME,true,false,false,null);
            channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,ROUTINGKEY,null);

            Consumer consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body,"UTF-8");
                    System.out.println("This is TopicConsumerB with 'zhouyuan.revolution.#' routingkey" +
                            "\r\n" +
                            "Here are received messages:[" + message + "]");
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
