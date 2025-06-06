package com.api.authapi.api.producer;

import com.api.authapi.config.properties.RabbitProperties;
import com.api.authapi.domain.dtos.user.RegisterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitProducer {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitProperties rabbitProperties;

    public void sendMessageRegistrationResponse(RegisterResponse message) {
        rabbitTemplate.convertAndSend(
                rabbitProperties.getExchange(),
                rabbitProperties.getRoutingKey().getRoutingKeyRegistrationResponse(),
                message
        );
    }
}
