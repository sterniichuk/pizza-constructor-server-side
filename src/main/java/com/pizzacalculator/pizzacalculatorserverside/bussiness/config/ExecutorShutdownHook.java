package com.pizzacalculator.pizzacalculatorserverside.bussiness.config;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class ExecutorShutdownHook implements DisposableBean {

    private final ExecutorService executorService;

    @Autowired
    public ExecutorShutdownHook(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void destroy() {
        executorService.shutdown();
    }
}

