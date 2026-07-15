package com.smart.elderly.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_NOTIFY_EXCHANGE = "order.notify.exchange";
    public static final String ORDER_NOTIFY_QUEUE = "order.notify.queue";
    public static final String ORDER_NOTIFY_ROUTING_KEY = "order.notify.#";

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
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(3);
        return factory;
    }
}