package org.homework.utils;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homework.enums.OperationType;
import org.homework.pojo.po.Log;
import org.homework.pojo.po.OperationLog;
import org.homework.pojo.po.User;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;

@Component
@Slf4j
@RequiredArgsConstructor
public class MQUtil {
    public static final String OPERATION_LOG_QUEUE = "operation_log.queue";
    public static final String QUEUE_NAME = "api_log.queue";
    public static final String OPERATION_LOG_TABLE = "operation_log";
    public static final String API_LOG_TABLE = "api_log";
    public static boolean MQ_HEALTHY = true;
    private final RabbitTemplate rabbitTemplate;

    private final MongoUtil mongoUtil;
    private final ExecutorService executorService;

    @Value("${spring.application.name}")
    private String serverName;

    public void sendOperationLogM(OperationType type, String description) {
        User user = SecurityInfoUtil.getUser();
        OperationLog operationLog = OperationLog.builder()
                // 谁
                .operationUserId(user.getId())
                // 哪个时间
                .operationTime(LocalDateTime.now())
                .operationUser(user.getRealName())
                .type(type.getTypeString())
                // 干了什么
                .description(description).build();
        if (MQ_HEALTHY) {
            String s = JSON.toJSONString(operationLog);
            try {
                rabbitTemplate.convertAndSend(OPERATION_LOG_QUEUE, s);
            } catch (AmqpException e) {
                executorService.execute(() -> mongoUtil.insert(operationLog, OPERATION_LOG_TABLE));
                mqExceptionHandler(operationLog, OPERATION_LOG_TABLE);
            }
        } else {
            executorService.execute(() -> mongoUtil.insert(operationLog, OPERATION_LOG_TABLE));
        }
    }

    public void sendApiLogM(String requestPath, String requestMethod,
                            String requestArgs, String ip, String code) {

        Log aLog = Log.builder()
                .serverName(serverName)
                .requestPath(requestPath)
                .requestMethod(requestMethod)
                .requestArgs(requestArgs)
                .ip(ip)
                .responseCode(String.valueOf(code))
                .requestTime(LocalDateTime.now())
                .build();

        if (MQUtil.MQ_HEALTHY) {
            try {
                String str = JSON.toJSONString(aLog);
                rabbitTemplate.convertAndSend(QUEUE_NAME, str);
            } catch (AmqpException e) {
                executorService.execute(() -> mongoUtil.insert(aLog, "api_log"));
                mqExceptionHandler(aLog, API_LOG_TABLE);
            }
        } else {
            // 异步写日志到 mongodb
            executorService.execute(() -> mongoUtil.insert(aLog, "api_log"));
        }
    }

    public <T> void mqExceptionHandler(T o, String tableName) {
        log.info("mq disconnected");
        // TODO 发送异常邮件
        MQ_HEALTHY = false;
        executorService.execute(() -> mongoUtil.insert(o, tableName));
    }

}
