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
    private Exchange exchange;
    private Queue queue;
    private RoutingKey routingKey;

    @Getter
    @Setter
    public static class Exchange {
        private String auth;
    }

    @Getter
    @Setter
    public static class Queue {
        private String initiateUserRegistrationCommand;
        private String rollbackUserRegistrationCommand;
        private String confirmUserRegistrationCommand;
        private String failureUserRegistrationReply;
        private String successUserRegistrationReply;
    }

    @Getter
    @Setter
    public static class RoutingKey {
        private String initiateUserRegistrationCommand;
        private String rollbackUserRegistrationCommand;
        private String confirmUserRegistrationCommand;
        private String failureUserRegistrationReply;
        private String successUserRegistrationReply;
    }
}
