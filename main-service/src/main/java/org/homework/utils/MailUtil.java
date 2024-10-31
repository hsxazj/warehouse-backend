package org.homework.utils;

import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author zhanghaifeng
 */
@Component
@Slf4j
public class MailUtil {

    @Value("${spring.mail.username}")
    private String sender; // 邮件发送者

    private String TARGET_EMAIL = "2045714255@qq.com";

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private JavaMailSender mailSender;

    public void sendErrorLog(String description, String apiLogPath, String contactDetails) throws Exception {
        Preconditions.checkArgument(!description.isBlank(), "请填写反馈内容");
        Preconditions.checkArgument(!contactDetails.isBlank() && emailCheck(contactDetails),
                "请输入正确的邮箱");
        RLock sendErrorLog = redissonClient.getLock("sendErrorLog");
        boolean isSuccess = false;
        try {
            isSuccess = sendErrorLog.tryLock(-1, TimeUnit.MILLISECONDS);
            if (!isSuccess) {
                throw new RuntimeException("服务器中存在相同的任务（可能由他人提交），请稍后再试");
            }
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(sender);
            helper.setTo(TARGET_EMAIL);
            helper.setSubject("BUG反馈");
            String text = "描述：<br/>&nbsp;&nbsp;&nbsp;"
                    + description
                    + "<br/>联系方式："
                    + contactDetails;
            helper.setText(text, true);
            //File file = new File(path);
            //if (!file.exists()) {
            //    throw new RuntimeException("error日志不存在");
            //}
            // TODO 添加basePath
            File apiLog = new File(apiLogPath);
            if (!apiLog.exists()) {
                throw new RuntimeException("未正确生成api日志");
            }
            //helper.addAttachment(file.getName(), new FileSystemResource(file));
            helper.addAttachment(apiLog.getName(), new FileSystemResource(apiLog));
            mailSender.send(mimeMessage);
        } finally {
            if (isSuccess && sendErrorLog.isHeldByCurrentThread()) {
                sendErrorLog.unlock();
            }
        }
    }

    public void sendSuggestion(String suggestion, String contactDetails) throws MessagingException {
        Preconditions.checkArgument(!suggestion.isBlank(), "请填写反馈内容");
        Preconditions.checkArgument(!contactDetails.isBlank() && emailCheck(contactDetails)
                , "请输入正确的邮箱");
        log.info("用户反馈：{}，联系方式：{}", suggestion, contactDetails);
        String text = "建议：<br/>&nbsp;&nbsp;&nbsp;"
                + suggestion
                + "<br/>联系方式："
                + contactDetails;
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject("优化建议");
        helper.setFrom(sender);
        helper.setTo(TARGET_EMAIL);
        helper.setText(text, true);
        mailSender.send(mimeMessage);
    }

    public boolean emailCheck(String email) {
        return email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    }
}
