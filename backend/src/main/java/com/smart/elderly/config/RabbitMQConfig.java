package com.smart.elderly.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
@EnableRabbit
@Slf4j
public class RabbitMQConfig {

    public static final String ORDER_NOTIFY_EXCHANGE = "order.notify.exchange";
    public static final String ORDER_NOTIFY_QUEUE = "order.notify.queue";
    public static final String ORDER_NOTIFY_ROUTING_KEY = "order.notify.#";

    @Value("${spring.rabbitmq.listener.simple.auto-startup:true}")
    private boolean autoStartup;

    private final ConnectionFactory connectionFactory;

    public RabbitMQConfig(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @PostConstruct
    public void init() {
        log.info("RabbitMQ配置初始化完成, autoStartup={}", autoStartup);
    }

    @Bean
    public TopicExchange orderNotifyExchange() {
        return ExchangeBuilder.topicExchange(ORDER_NOTIFY_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue orderNotifyQueue() {
        return QueueBuilder.durable(ORDER_NOTIFY_QUEUE)
                .build();
    }

    @Bean
    public Binding orderNotifyBinding(TopicExchange orderNotifyExchange, Queue orderNotifyQueue) {
        return BindingBuilder.bind(orderNotifyQueue)
                .to(orderNotifyExchange)
                .with(ORDER_NOTIFY_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(3);
        factory.setAutoStartup(autoStartup);
        return factory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(autoStartup);
        return admin;
    }

    public boolean isRabbitMQAvailable() {
        try {
            if (connectionFactory instanceof CachingConnectionFactory) {
                ((CachingConnectionFactory) connectionFactory).createConnection().close();
                return true;
            }
            connectionFactory.createConnection().close();
            return true;
        } catch (Exception e) {
            log.warn("RabbitMQ连接不可用: {}", e.getMessage());
            return false;
        }
    }
}