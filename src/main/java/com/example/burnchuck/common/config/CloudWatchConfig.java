package com.example.burnchuck.common.config;

import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

@Configuration
public class CloudWatchConfig {

    @Bean
    public CloudWatchMeterRegistry cloudWatchMeterRegistry() {

        io.micrometer.cloudwatch2.CloudWatchConfig config = new io.micrometer.cloudwatch2.CloudWatchConfig() {
            @Override
            public String get(String key) {
                return null;
            }

            @Override
            public String namespace() {
                return "burn-chuck";
            }
        };

        return new CloudWatchMeterRegistry(
                config,
                Clock.SYSTEM,
                CloudWatchAsyncClient.create()
        );
    }
}