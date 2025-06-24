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
        private String userRegister;
    }

    @Getter
    @Setter
    public static class Queue {
        private String userRegisterCommand;
        private String userRegisterReply;
        private String compensateUserRegisterCommand;
    }

    @Getter
    @Setter
    public static class RoutingKey {
        private String userRegisterCommand;
        private String userRegisterReply;
        private String compensateUserRegisterCommand;
    }
}
