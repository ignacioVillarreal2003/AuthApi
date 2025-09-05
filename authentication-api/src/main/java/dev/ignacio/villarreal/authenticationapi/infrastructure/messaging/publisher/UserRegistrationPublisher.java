package dev.ignacio.villarreal.authenticationapi.infrastructure.messaging.publisher;

import dev.ignacio.villarreal.authenticationapi.config.properties.RabbitProperties;
import dev.ignacio.villarreal.authenticationapi.domain.saga.reply.AwaitingVerificationUserRegistrationReply;
import dev.ignacio.villarreal.authenticationapi.domain.saga.reply.FailureUserRegistrationReply;
import dev.ignacio.villarreal.authenticationapi.domain.saga.reply.SuccessUserRegistrationReply;
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
