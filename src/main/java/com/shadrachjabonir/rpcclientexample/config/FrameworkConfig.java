package com.shadrachjabonir.rpcclientexample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;

@Configuration
@Lazy
public class FrameworkConfig {

    @Bean
    public DirectClientConfig directServerConfig() {
        return new DirectClientConfig();
    }

    @Bean
    public QueueClientConfig queueClientConfig() {
        return new QueueClientConfig();
    }
}
