package com.example.library.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "library.notification.exchange";
    public static final String QUEUE = "library.notification.queue";
    public static final String ROUTING_KEY = "notification.create";

    @Bean
    DirectExchange notificationExchange() { return new DirectExchange(EXCHANGE); }
    @Bean Queue notificationQueue() { return QueueBuilder.durable(QUEUE).build(); }
    @Bean
    Binding binding() {
        return BindingBuilder.bind(notificationQueue()).to(notificationExchange()).with(ROUTING_KEY);
    }
    @Bean MessageConverter jsonConverter() { return new Jackson2JsonMessageConverter(); }
}