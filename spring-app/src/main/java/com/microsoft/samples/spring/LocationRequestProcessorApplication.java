package com.microsoft.samples.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.autoconfigure.context.ContextCredentialsAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.context.ContextRegionProviderAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;


@SpringBootApplication
@EnableAutoConfiguration(exclude = {ContextInstanceDataAutoConfiguration.class, ContextCredentialsAutoConfiguration.class, ContextRegionProviderAutoConfiguration.class, ContextStackAutoConfiguration.class})
public class LocationRequestProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocationRequestProcessorApplication.class, args);
    }

}
