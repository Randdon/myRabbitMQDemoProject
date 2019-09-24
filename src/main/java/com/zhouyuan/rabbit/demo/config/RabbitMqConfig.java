package com.zhouyuan.rabbit.demo.config;

import com.zhouyuan.rabbit.demo.rabbitlistener.SimpleListener;
import com.zhouyuan.rabbit.demo.rabbitlistener.UserOrderListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


@Configuration
public class RabbitMqConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqConfig.class);

    @Autowired
    private Environment environment;

    @Autowired
    private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;

    @Autowired
    private CachingConnectionFactory connectionFactory;

    /**
     * 单一消费者容器工厂
     * @return
     */
    @Bean(name = "singleListenerContainer")
    public SimpleRabbitListenerContainerFactory listenerContainer(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        factory.setTxSize(1);
        return factory;
    }

    /**
     * 多个消费者容器工厂
     * @return
     */
    @Bean
    public SimpleRabbitListenerContainerFactory multiLisenerContainer(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factoryConfigurer.configure(factory,connectionFactory);//为什么不用factory.setConnectionFactory(connectionFactory);？
        factory.setMessageConverter(new Jackson2JsonMessageConverter());

        //这里是为这个容器工厂确定消息确认机制，这里的设置会覆盖掉application.properties里的配置
        factory.setAcknowledgeMode(AcknowledgeMode.NONE);

        factory.setConcurrentConsumers(environment.getProperty("spring.rabbitmq.listener.simple.concurrency",int.class));
        factory.setMaxConcurrentConsumers(environment.getProperty("spring.rabbitmq.listener.simple.max-concurrency",int.class));
        factory.setPrefetchCount(environment.getProperty("spring.rabbitmq.listener.simple.prefetch",int.class));
        return factory;
    }
    @Bean
    public RabbitTemplate rabbitTemplate(){
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        /**
         * 当mandatory标志位设置为true时
         * 如果exchange根据自身类型和消息routingKey无法找到一个合适的queue存储消息
         * 那么broker会调用basic.return方法将消息返还给生产者
         * 当mandatory设置为false时，出现上述情况broker会直接将消息丢弃
         */
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("消息发送成功：correlationData({}),ack({}),cause({})",correlationData,ack,cause);
            }
        });
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.info("消息丢失：message({}),replyCode({}),replyText({}),exchange({}),routingKey({})",
                        message,replyCode,replyText,exchange,routingKey);
            }
        });
        return rabbitTemplate;
    }

    /**
     * 构建基本消息模型
     * 这里的@Bean的作用应该是当项目启动时，会连接到rabbitmq的服务器，然后就会通过Bean注入的方式创建这些交换机、队列和路由
     * 也就是说在项目启动的时候就会创建好交换机和队列以及路由
     */

    @Bean
    public DirectExchange basicExchange(){
        return new DirectExchange(environment.getProperty("rabbitmq.simple.message.exchange.name"),
                true,false);
    }

    @Bean
    public Queue basicQueue(){
        return new Queue(environment.getProperty("rabbitmq.simple.message.queue.name"),
                true);
    }

    @Bean
    public Binding basicBinding(){
        return BindingBuilder.bind(basicQueue()).to(basicExchange())
                .with(environment.getProperty("rabbitmq.simple.message.routingKey.name"));
    }

    /**
     * 构建抢单消息模型
     */
    @Bean
    public DirectExchange robExchange(){
        return new DirectExchange(environment.getProperty("rabbitmq.rob.product.exchange.name"),
                true,true);
    }

    @Bean
    public Queue robQueue(){
        return new Queue(environment.getProperty("rabbitmq.rob.product.queue.name"),true);
    }

    @Bean
    public Binding robBind(){
        return BindingBuilder.bind(robQueue()).to(robExchange()).with(
                environment.getProperty("rabbitmq.rob.product.routingKey.name")
        );
    }

    @Bean
    public Queue simpleQueue(){
        return new Queue(environment.getProperty("rabbitmq.simple.container.queue.name"),true);
    }

    @Bean
    public TopicExchange simpleExchange(){
        return new TopicExchange(environment.getProperty("rabbitmq.simple.container.exchange.name"),
                true,false);
    }

    @Bean
    public Binding simpleBingding(){
        return BindingBuilder.bind(simpleQueue())
                .to(simpleExchange())
                .with(environment.getProperty("rabbitmq.simple.container.routingKey.name"));
    }

    @Autowired
    SimpleListener simpleListener;
    /**
     * 并发配置-消息确认机制-Listener
     * @return
     */
    @Bean(name = "simpleContainer")
    public SimpleMessageListenerContainer simpleContainer(@Qualifier("simpleQueue") Queue simpleQueue){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();

        container.setConnectionFactory(connectionFactory);
        //视频中用的是container.setMessageConverter(new Jackson2JsonMessageConverter());但这个方法已不被推荐使用了
        container.setMessagePropertiesConverter(new DefaultMessagePropertiesConverter());

        /**
         * 并发配置
         */
        container.setConcurrentConsumers(environment.getProperty("spring.rabbitmq.listener.simple.concurrency",Integer.class));
        container.setMaxConcurrentConsumers(environment.getProperty("spring.rabbitmq.listener.simple.max-concurrency",Integer.class));
        container.setPrefetchCount(environment.getProperty("spring.rabbitmq.listener.simple.prefetch",Integer.class));

        /**
         * 消息确认机制配置
         */
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setQueues(simpleQueue);
        container.setMessageListener(simpleListener);
        return container;
    }

    /**
     * 用户商城下单实战
     */

    @Bean
    public TopicExchange userOrderExchange(){
        return new TopicExchange(environment.getProperty("rabbitmq.user.order.exchange.name"));
    }

    @Bean
    public Queue userOrderQueue(){
        return new Queue(environment.getProperty("rabbitmq.user.order.queue.name"));
    }

    @Bean
    public Binding userOrderBinding(){
        return BindingBuilder.bind(userOrderQueue())
                .to(userOrderExchange())
                .with(environment.getProperty("rabbitmq.user.order.routingKey.name"));
    }

    @Autowired
    UserOrderListener userOrderListener;

    @Bean
    public SimpleMessageListenerContainer userOrderContainer(@Qualifier("userOrderQueue") Queue userOrderQueue){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setMessagePropertiesConverter(new DefaultMessagePropertiesConverter());

        container.setQueues(userOrderQueue);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(userOrderListener);

        return container;
    }
}
