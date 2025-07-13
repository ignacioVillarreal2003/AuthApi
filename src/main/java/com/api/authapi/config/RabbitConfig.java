package com.api.authapi.config;

import com.api.authapi.config.properties.RabbitProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
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
    public TopicExchange authExchange() {
        return new TopicExchange(rabbitProperties.getExchange().getAuth(), true, false);
    }

    @Bean
    public DirectExchange authDlx() {
        return new DirectExchange(rabbitProperties.getExchange().getAuth() + ".dlx", true, false);
    }

    @Bean
    public Queue userRegisterCommandQueue() {
        return QueueBuilder.durable(rabbitProperties.getQueue().getUserRegisterCommand())
                .withArgument("x-dead-letter-exchange", rabbitProperties.getExchange().getAuth() + ".dlx")
                .withArgument("x-dead-letter-routing-key", rabbitProperties.getRoutingKey().getUserRegisterCommand() + ".dlq")
                .build();
    }

    @Bean
    public Queue userRegisterCommandDlq() {
        return QueueBuilder.durable(rabbitProperties.getQueue().getUserRegisterCommand() + ".dlq").build();
    }

    @Bean
    public Queue userRegisterReplyQueue() {
        return QueueBuilder.durable(rabbitProperties.getQueue().getUserRegisterReply()).build();
    }

    @Bean
    public Queue compensateUserRegisterCommandQueue() {
        return QueueBuilder.durable(rabbitProperties.getQueue().getCompensateUserRegisterCommand()).build();
    }

    @Bean
    public Binding bindingUserRegisterCommand() {
        return BindingBuilder
                .bind(userRegisterCommandQueue())
                .to(authExchange())
                .with(rabbitProperties.getRoutingKey().getUserRegisterCommand());
    }

    @Bean
    public Binding bindingUserRegisterDlq() {
        return BindingBuilder
                .bind(userRegisterCommandDlq())
                .to(authDlx())
                .with(rabbitProperties.getRoutingKey().getUserRegisterCommand() + ".dlq");
    }

    @Bean
    public Binding bindingUserRegisterReply() {
        return BindingBuilder
                .bind(userRegisterReplyQueue())
                .to(authExchange())
                .with(rabbitProperties.getRoutingKey().getUserRegisterReply());
    }

    @Bean
    public Binding bindingCompensateUserRegisterCommand() {
        return BindingBuilder
                .bind(compensateUserRegisterCommandQueue())
                .to(authExchange())
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

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        factory.setReceiveTimeout(30_000L);
        factory.setDefaultRequeueRejected(false);

        return factory;
    }
}
