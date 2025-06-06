package com.api.authapi.config;

import com.api.authapi.config.properties.RabbitProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitConfig {

    private final RabbitProperties rabbitProperties;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(rabbitProperties.getExchange());
    }

    @Bean
    public Queue queueRegistrationRequest() {
        return new Queue(rabbitProperties.getQueue().getQueueRegistrationRequest());
    }

    @Bean
    public Queue queueRegistrationResponse() {
        return new Queue(rabbitProperties.getQueue().getQueueRegistrationResponse());
    }

    @Bean
    public Queue queueRegistrationRollback() {
        return new Queue(rabbitProperties.getQueue().getQueueRegistrationRollback());
    }

    @Bean
    public Binding bindingRegistrationRequest() {
        return BindingBuilder
                .bind(queueRegistrationRequest())
                .to(exchange())
                .with(rabbitProperties.getQueue().getQueueRegistrationRequest());
    }

    @Bean
    public Binding bindingRegistrationResponse() {
        return BindingBuilder
                .bind(queueRegistrationResponse())
                .to(exchange())
                .with(rabbitProperties.getQueue().getQueueRegistrationResponse());
    }

    @Bean
    public Binding bindingRegistrationRollback() {
        return BindingBuilder
                .bind(queueRegistrationRollback())
                .to(exchange())
                .with(rabbitProperties.getQueue().getQueueRegistrationRollback());
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
