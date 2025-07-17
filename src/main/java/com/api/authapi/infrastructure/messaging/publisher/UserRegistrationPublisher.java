package com.api.authapi.infrastructure.messaging.publisher;

import com.api.authapi.config.properties.RabbitProperties;

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
        log.info("[UserRegistrationPublisher::publishSuccessUserRegistrationReply] Publishing success reply. sagaId={}", reply.getSagaId());
        rabbitTemplate.convertAndSend(
                rabbitProperties.getExchange().getAuth(),
                rabbitProperties.getRoutingKey().getSuccessUserRegistrationReply(),
                reply
        );
        log.info("[UserRegistrationPublisher::publishSuccessUserRegistrationReply] Success reply published. sagaId={}", reply.getSagaId());
    }

    public void publishFailureUserRegistrationReply(FailureUserRegistrationReply reply) {
        log.info("[UserRegistrationPublisher::publishFailureUserRegistrationReply] Publishing failure reply. sagaId={}, status={}, message={}",
                reply.getSagaId(), reply.getStatus(), reply.getMessage());
        rabbitTemplate.convertAndSend(
                rabbitProperties.getExchange().getAuth(),
                rabbitProperties.getRoutingKey().getFailureUserRegistrationReply(),
                reply
        );
        log.info("[UserRegistrationPublisher::publishFailureUserRegistrationReply] Failure reply published. sagaId={}", reply.getSagaId());
    }
}
