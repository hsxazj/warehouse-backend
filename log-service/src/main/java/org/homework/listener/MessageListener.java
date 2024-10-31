package org.homework.listener;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.homework.pojo.po.Log;
import org.homework.pojo.po.OperationLog;
import org.homework.utils.MongoUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageListener {

    @Resource
    private MongoUtil mongoUtil;

    @RabbitListener(queues = "api_log.queue")
    public void apiLogRecord(String msg) {
        Log aLog = null;
        try {
            aLog = JSON.parseObject(msg, Log.class);
            log.info("=============================");
            log.info("服务名称:{}", aLog.getServerName());
            log.info("请求路径:{}", aLog.getRequestPath());
            log.info("请求方法:{}", aLog.getRequestMethod());
            log.info("请求参数:{}", aLog.getRequestArgs());
            log.info("IP:{}", aLog.getIp());
            log.info("响应码:{}", aLog.getResponseCode());
            log.info("=============================");
        } catch (Exception e) {
            log.info("log 格式错误");
        }
        if (aLog != null) {
            mongoUtil.insert(aLog, "api_log");
        }
    }

    @RabbitListener(queues = "operation_log.queue")
    public void operationLogRecord(String msg) {
        // 解析OperationLog
        OperationLog operationLog = JSON.parseObject(msg, OperationLog.class);
        mongoUtil.insert(operationLog, "operation_log");
    }


}
