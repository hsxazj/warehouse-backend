package org.homework.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * redisson 配置
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Slf4j
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.password}")
    private String password;
    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.database}")
    private int database;

    @Bean
    public RedissonClient redissonClient() {
        // 单点
        Config config = new Config();
        String address = "redis://" + host + ":" + port;
        // 地址及密码
        config
                .useSingleServer()
                .setAddress(address)
                .setPassword(password)
                .setDatabase(database)
        ;
        return Redisson.create(config);
    }
}
