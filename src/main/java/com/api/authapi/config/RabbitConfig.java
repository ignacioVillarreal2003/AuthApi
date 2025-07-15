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
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@RequiredArgsConstructor
@EnableRetry
public class RabbitConfig {

    private final RabbitProperties rabbitProperties;

    @Bean
    public TopicExchange authExchange() {
        return new TopicExchange(rabbitProperties.getExchange().getAuth(), true, false);
    }

    @Bean
    public Queue userRegisterInitialCommandQueue() {
        return QueueBuilder.durable(rabbitProperties.getQueue().getUserRegisterInitialCommand()).build();
    }

    @Bean
    public Queue userRegisterSuccessReplyQueue() {
        return QueueBuilder.durable(rabbitProperties.getQueue().getUserRegisterSuccessReply()).build();
    }

    @Bean
    public Queue userRegisterFailureReplyQueue() {
        return QueueBuilder.durable(rabbitProperties.getQueue().getUserRegisterFailureReply()).build();
    }

    @Bean
    public Queue userRegisterCompensationCommandQueue() {
        return QueueBuilder.durable(rabbitProperties.getQueue().getUserRegisterCompensationCommand()).build();
    }

    @Bean
    public Queue userRegisterConfirmationCommandQueue() {
        return QueueBuilder.durable(rabbitProperties.getQueue().getUserRegisterConfirmationCommand()).build();
    }

    @Bean
    public Binding bindingUserRegisterInitialCommand() {
        return BindingBuilder
                .bind(userRegisterInitialCommandQueue())
                .to(authExchange())
                .with(rabbitProperties.getRoutingKey().getUserRegisterInitialCommand());
    }

    @Bean
    public Binding bindingUserRegisterSuccessReply() {
        return BindingBuilder
                .bind(userRegisterSuccessReplyQueue())
                .to(authExchange())
                .with(rabbitProperties.getRoutingKey().getUserRegisterSuccessReply());
    }

    @Bean
    public Binding bindingUserRegisterFailureReply() {
        return BindingBuilder
                .bind(userRegisterFailureReplyQueue())
                .to(authExchange())
                .with(rabbitProperties.getRoutingKey().getUserRegisterFailureReply());
    }

    @Bean
    public Binding bindingUserRegisterCompensationCommand() {
        return BindingBuilder
                .bind(userRegisterCompensationCommandQueue())
                .to(authExchange())
                .with(rabbitProperties.getRoutingKey().getUserRegisterCompensationCommand());
    }

    @Bean
    public Binding bindingUserRegisterConfirmationCommand() {
        return BindingBuilder
                .bind(userRegisterConfirmationCommandQueue())
                .to(authExchange())
                .with(rabbitProperties.getRoutingKey().getUserRegisterConfirmationCommand());
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
