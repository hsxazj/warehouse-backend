package org.homework.task;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.homework.conventioin.result.Result;
import org.homework.utils.MQUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Component
@Slf4j
public class MQHealthyCheck {

    @Resource
    private RestTemplate restTemplate;

    @Value("${spring.rabbitmq.username}")
    private String userName;
    @Value("${spring.rabbitmq.password}")
    private String password;
    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.check_port}")
    private String checkPort;

    @Async
    @Scheduled(fixedRate = 30000)
    void check() {

        // 创建RestTemplate实例
        RestTemplate restTemplate = new RestTemplate();

        // 设置URL
        String url = "http://" + host + ":" + checkPort + "/api/overview";

        // 设置HTTP头部，包括认证信息
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization",
                "Basic " + new String(Base64.getEncoder().encode((userName + ":" + password).getBytes())));

        // 创建HttpEntity对象，包含头部信息
        HttpEntity<String> entity = new HttpEntity<>(headers);

        boolean isHealthy = false;

        for (int i = 0; i < 5; i++) {
            try {
                restTemplate.exchange(url, HttpMethod.GET, entity, Result.class).getStatusCode();
            } catch (RestClientException e) {
                continue;
            }
            isHealthy = true;
            break;
        }

        // 执行GET请求
        if (isHealthy) {
            log.info("MQ正常在线");
            if (!MQUtil.MQ_HEALTHY) {
                MQUtil.MQ_HEALTHY = true;
            }
        } else {
            log.error("MQ掉线");
            if (MQUtil.MQ_HEALTHY) {
                MQUtil.MQ_HEALTHY = false;
            }
        }
    }

}
