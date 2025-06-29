package com.api.authapi.api.producers;

import com.api.authapi.config.properties.RabbitProperties;
import com.api.authapi.domain.dtos.user.RegisterUserReply;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterUserSagaPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitProperties rabbitProperties;

    public void publishRegisterUserReply(RegisterUserReply message) {
        rabbitTemplate.convertAndSend(
                rabbitProperties.getExchange().getAuth(),
                rabbitProperties.getRoutingKey().getUserRegisterReply(),
                message
        );
    }
}
