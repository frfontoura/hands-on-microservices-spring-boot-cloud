package com.microservices.core.recommendation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.microservices")
public class RecommendationServiceApplication {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceApplication.class);

    public static void main(final String[] args) {
        final ConfigurableApplicationContext ctx = SpringApplication.run(RecommendationServiceApplication.class, args);

        final String mongoDbHost = ctx.getEnvironment().getProperty("spring.data.mongodb.host");
        final String mongoDbPort = ctx.getEnvironment().getProperty("spring.data.mongodb.port");
        LOG.info("Connected to MongoDb: " + mongoDbHost + ":" + mongoDbPort);
    }

}
