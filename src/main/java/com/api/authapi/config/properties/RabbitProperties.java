package com.api.authapi.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rabbit")
@Getter
@Setter
public class RabbitProperties {
    private String exchange;
    private Queue queue;
    private RoutingKey routingKey;

    @Getter
    @Setter
    public static class Queue {
        private String queueRegistrationRequest;
        private String queueRegistrationResponse;
        private String queueRegistrationRollback;
    }

    @Getter
    @Setter
    public static class RoutingKey {
        private String routingKeyRegistrationRequest;
        private String routingKeyRegistrationResponse;
        private String routingKeyRegistrationRollback;
    }
}
