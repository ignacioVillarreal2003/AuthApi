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
    public Queue initiateUserRegistrationCommandQueue() {
        return QueueBuilder.durable(rabbitProperties.getQueue().getInitiateUserRegistrationCommand()).build();
    }

    @Bean
    public Queue successUserRegistrationReplyQueue() {
        return QueueBuilder.durable(rabbitProperties.getQueue().getSuccessUserRegistrationReply()).build();
    }

    @Bean
    public Queue failureUserRegistrationReplyQueue() {
        return QueueBuilder.durable(rabbitProperties.getQueue().getFailureUserRegistrationReply()).build();
    }

    @Bean
    public Queue awaitingVerificationUserRegistrationReplyQueue() {
        return QueueBuilder.durable(rabbitProperties.getQueue().getAwaitingVerificationUserRegistrationReply()).build();
    }

    @Bean
    public Queue rollbackUserRegistrationCommandQueue() {
        return QueueBuilder.durable(rabbitProperties.getQueue().getRollbackUserRegistrationCommand()).build();
    }

    @Bean
    public Queue confirmUserRegistrationCommandQueue() {
        return QueueBuilder.durable(rabbitProperties.getQueue().getConfirmUserRegistrationCommand()).build();
    }

    @Bean
    public Binding bindingInitiateUserRegistrationCommand() {
        return BindingBuilder
                .bind(initiateUserRegistrationCommandQueue())
                .to(authExchange())
                .with(rabbitProperties.getRoutingKey().getInitiateUserRegistrationCommand());
    }

    @Bean
    public Binding bindingSuccessUserRegistrationReply() {
        return BindingBuilder
                .bind(successUserRegistrationReplyQueue())
                .to(authExchange())
                .with(rabbitProperties.getRoutingKey().getSuccessUserRegistrationReply());
    }

    @Bean
    public Binding bindingFailureUserRegistrationReplyQueue() {
        return BindingBuilder
                .bind(failureUserRegistrationReplyQueue())
                .to(authExchange())
                .with(rabbitProperties.getRoutingKey().getFailureUserRegistrationReply());
    }

    @Bean
    public Binding bindingAwaitingVerificationUserRegistrationReplyQueue() {
        return BindingBuilder
                .bind(awaitingVerificationUserRegistrationReplyQueue())
                .to(authExchange())
                .with(rabbitProperties.getRoutingKey().getAwaitingVerificationUserRegistrationReply());
    }


    @Bean
    public Binding bindingRollbackUserRegistrationCommand() {
        return BindingBuilder
                .bind(rollbackUserRegistrationCommandQueue())
                .to(authExchange())
                .with(rabbitProperties.getRoutingKey().getRollbackUserRegistrationCommand());
    }

    @Bean
    public Binding bindingConfirmUserRegistrationCommandQueue() {
        return BindingBuilder
                .bind(confirmUserRegistrationCommandQueue())
                .to(authExchange())
                .with(rabbitProperties.getRoutingKey().getConfirmUserRegistrationCommand());
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
