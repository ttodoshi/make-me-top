package org.example.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ExecutorConfig extends AsyncConfigurerSupport {
    @Override
    @Bean("asyncExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor poolExecutor = new ThreadPoolTaskExecutor();
        poolExecutor.setTaskDecorator(new ContextCopyingDecorator());
        poolExecutor.initialize();
        return poolExecutor;
    }
}