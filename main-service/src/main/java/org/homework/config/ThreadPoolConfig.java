package org.homework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class ThreadPoolConfig {

    /**
     * 不要使用 Executors 创建线程，从它的源码中可以看出，Executors 会 new 一个 Integer.MAX_VALUE 大小的 阻塞队列 作为线程池的参数
     * 这可能会造成 OOM
     */
    @Bean(name = "executorService")
    public ExecutorService executorService() {

        return new ThreadPoolExecutor(2,
                10,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(50),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

}
