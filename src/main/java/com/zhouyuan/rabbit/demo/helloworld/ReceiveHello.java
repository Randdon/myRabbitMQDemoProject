package com.zhouyuan.rabbit.demo.helloworld;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReceiveHello {

    private static final String QUEUE_NAME = "ZhouYuan:Rabbit:Test";

    public static void main(String[] args) {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.253");
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            //第一个Boolean型的参数的意义是是否持久化
            channel.queueDeclare(QUEUE_NAME,false,false,false,null);
            System.out.println("ZhouYuan is waiting for messages. To exit press CTRL+C");

/*
            //官网demo的写法：
            DeliverCallback deliverCallback = (consumerTag,deliver) -> {
                String message = new String(deliver.getBody(),"UTF-8");
                System.out.println("ZhouYuan received the message from RabbitMQ:'" + message + "'" );
            };
            channel.basicConsume(QUEUE_NAME,deliverCallback,consumerTag -> {});
*/

            //视频教学的写法：
            Consumer consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body,"UTF-8");
                    System.out.println("ZhouYuan received the message from RabbitMQ:'" + message + "'" );
                }
            };
            /**
             * boolean型参数代表是否开启确认消费
             * 1.消费者消费消息之后需要ack确认消费：
             * 	a.回调告知mq 某条队列中的 消息已经被我consumer消费了 - 如果不设置，mq是不会把指定的queue的消息推给你的
             * 	b.autoAck ：true-确认消费，false-没有确认消费，消息依旧存在queue里面-下次再启动时 mq会重新分发message
             *
             * 	测试方法：将Boolean参数改为false，先跑ReceiveHello程序，再跑SendHello程序，可以看到接收到消息，
             * 	消息的浏览器控制台界面可以看到unacked参数变为1了，
             * 	再把参数改为true后，重启ReceiveHello程序，可以看到同样的消息又接收到了，这便是因为上次没有确认消费造成的重复消费
             * 	这时再去控制台界面查看unacked参数又会变成0，因为被确认消费了
             * 	再重启一次ReceiveHello程序可以看到不会再接收到该条消息了，因为已经被确认消费了
             */
            channel.basicConsume(QUEUE_NAME,true,consumer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
