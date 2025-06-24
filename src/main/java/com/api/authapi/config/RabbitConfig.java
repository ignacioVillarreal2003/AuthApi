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
    public TopicExchange userRegisterExchange() {
        return new TopicExchange(rabbitProperties.getExchange().getUserRegister());
    }

    @Bean
    public Queue userRegisterCommandQueue() {
        return new Queue(rabbitProperties.getQueue().getUserRegisterCommand());
    }

    @Bean
    public Queue userRegisterReplyQueue() {
        return new Queue(rabbitProperties.getQueue().getUserRegisterReply());
    }

    @Bean
    public Queue compensateUserRegisterCommandQueue() {
        return new Queue(rabbitProperties.getQueue().getCompensateUserRegisterCommand());
    }

    @Bean
    public Binding bindingUserRegisterCommand() {
        return BindingBuilder
                .bind(userRegisterCommandQueue())
                .to(userRegisterExchange())
                .with(rabbitProperties.getRoutingKey().getUserRegisterCommand());
    }

    @Bean
    public Binding bindingUserRegisterReply() {
        return BindingBuilder
                .bind(userRegisterReplyQueue())
                .to(userRegisterExchange())
                .with(rabbitProperties.getRoutingKey().getUserRegisterReply());
    }

    @Bean
    public Binding bindingCompensateUserRegisterCommand() {
        return BindingBuilder
                .bind(compensateUserRegisterCommandQueue())
                .to(userRegisterExchange())
                .with(rabbitProperties.getRoutingKey().getCompensateUserRegisterCommand());
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
