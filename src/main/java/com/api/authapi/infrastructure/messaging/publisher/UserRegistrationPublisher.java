package com.api.authapi.infrastructure.messaging.publisher;

import com.api.authapi.config.properties.RabbitProperties;

import com.api.authapi.domain.saga.reply.AwaitingVerificationUserRegistrationReply;
import com.api.authapi.domain.saga.reply.FailureUserRegistrationReply;
import com.api.authapi.domain.saga.reply.SuccessUserRegistrationReply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitProperties rabbitProperties;

    public void publishSuccessUserRegistrationReply(SuccessUserRegistrationReply reply) {
        rabbitTemplate.convertAndSend(
                rabbitProperties.getExchange().getAuth(),
                rabbitProperties.getRoutingKey().getSuccessUserRegistrationReply(),
                reply
        );
    }

    public void publishFailureUserRegistrationReply(FailureUserRegistrationReply reply) {
        rabbitTemplate.convertAndSend(
                rabbitProperties.getExchange().getAuth(),
                rabbitProperties.getRoutingKey().getFailureUserRegistrationReply(),
                reply
        );
    }

    public void publishAwaitingVerificationUserRegistrationReply(AwaitingVerificationUserRegistrationReply reply) {
        rabbitTemplate.convertAndSend(
                rabbitProperties.getExchange().getAuth(),
                rabbitProperties.getRoutingKey().getAwaitingVerificationUserRegistrationReply(),
                reply
        );
    }
}
