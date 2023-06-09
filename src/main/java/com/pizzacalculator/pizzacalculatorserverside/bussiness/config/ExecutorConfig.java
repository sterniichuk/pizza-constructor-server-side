package com.pizzacalculator.pizzacalculatorserverside.bussiness.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    @Bean
    public ExecutorService executorService() {
        // Create a thread pool with a fixed number of threads
        int threadPoolSize = 4; // Adjust the pool size as needed
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}

