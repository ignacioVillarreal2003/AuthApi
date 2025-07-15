package com.api.authapi.infrastructure.messaging.publisher;

import com.api.authapi.config.properties.RabbitProperties;

import com.api.authapi.domain.saga.reply.UserRegisterFailureReply;
import com.api.authapi.domain.saga.reply.UserRegisterSuccessReply;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRegistrationPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitProperties rabbitProperties;

    public void publishUserRegisterSuccessReply(UserRegisterSuccessReply message) {
        rabbitTemplate.convertAndSend(
                rabbitProperties.getExchange().getAuth(),
                rabbitProperties.getRoutingKey().getUserRegisterSuccessReply(),
                message
        );
    }

    public void publishUserRegisterFailureReply(UserRegisterFailureReply message) {
        rabbitTemplate.convertAndSend(
                rabbitProperties.getExchange().getAuth(),
                rabbitProperties.getRoutingKey().getUserRegisterFailureReply(),
                message
        );
    }
}
