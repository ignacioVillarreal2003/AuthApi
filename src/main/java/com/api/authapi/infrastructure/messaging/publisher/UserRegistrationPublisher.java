package com.api.authapi.infrastructure.messaging.publisher;

import com.api.authapi.config.properties.RabbitProperties;

import com.api.authapi.domain.saga.reply.UserRegisterFailureReply;
import com.api.authapi.domain.saga.reply.UserRegisterSuccessReply;
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

    public void publishUserRegisterSuccessReply(UserRegisterSuccessReply message) {
        log.info("[UserRegistrationPublisher::publishUserRegisterSuccessReply] Publishing success reply sagaId={}", message.getSagaId());
        rabbitTemplate.convertAndSend(
                rabbitProperties.getExchange().getAuth(),
                rabbitProperties.getRoutingKey().getUserRegisterSuccessReply(),
                message
        );
        log.info("[UserRegistrationPublisher::publishUserRegisterSuccessReply] Success reply published sagaId={}", message.getSagaId());
    }

    public void publishUserRegisterFailureReply(UserRegisterFailureReply message) {
        log.info("[UserRegistrationPublisher::publishUserRegisterFailureReply] Publishing failure reply sagaId={}, status={}, message={}",
                message.getSagaId(), message.getStatus(), message.getMessage());
        rabbitTemplate.convertAndSend(
                rabbitProperties.getExchange().getAuth(),
                rabbitProperties.getRoutingKey().getUserRegisterFailureReply(),
                message
        );
        log.info("[UserRegistrationPublisher::publishUserRegisterFailureReply] Failure reply published sagaId={}", message.getSagaId());
    }
}
