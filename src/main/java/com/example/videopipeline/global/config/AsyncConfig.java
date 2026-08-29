package com.example.videopipeline.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public ThreadPoolTaskExecutor applicationTaskExecutor(ThreadPoolTaskExecutorBuilder builder) {
        ThreadPoolTaskExecutor executor = builder.build();
        executor.setRejectedExecutionHandler((task, pool) ->
                log.warn("실행 큐 포화로 job 제출 거부 — 제출 전 상태(PENDING/FAILED) 그대로 남아 폴러가 다음 주기에 회수한다"));
        return executor;
    }
}
