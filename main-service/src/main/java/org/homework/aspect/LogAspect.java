package org.homework.aspect;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.homework.conventioin.result.Result;
import org.homework.utils.IpUtils;
import org.homework.utils.MQUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.support.MultipartFilter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Aspect
@Slf4j
public class LogAspect {

    private static final String QUEUE_NAME = "api_log.queue";

    private final MQUtil mqUtil;

    @Autowired
    public LogAspect(MQUtil mqUtil) {
        this.mqUtil = mqUtil;
    }

//    @Around("execution(* org.homework.controller..*(..))")
//    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
//        return recordLog(pjp);
//    }

    /**
     * 日志记录
     */
    public Object recordLog(ProceedingJoinPoint pjp) throws Throwable {
        String args = JSON.toJSONString(filterArgs(pjp.getArgs()));
        HttpServletRequest request = ((ServletRequestAttributes)
                Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String ip = IpUtils.getIpAddr(request);
        Exception exception = null;
        Integer code;
        Object proceed;
        Result result = null;
        boolean haveException = false;
        try {
            proceed = pjp.proceed();
            if (proceed instanceof Result) {
                result = (Result) proceed;
            } else {
                return Result.success();
            }
            code = result.getCode();
        } catch (Exception e) {
            haveException = true;
            code = 500;
            exception = e;
        }
        log.info("""
                                                
                        \r=======================================
                        \r请求地址:{}\s
                        \r请求方式:{}\s
                        \r请求参数:{}\s
                        \rip地址:{}\s
                        \r响应码:{}\s
                        \r=======================================
                        \r""",
                request.getRequestURI(),
                request.getMethod(),
                args,
                ip,
                code
        );
        mqUtil.sendApiLogM(
                request.getRequestURI(),
                request.getMethod(), args, ip, String.valueOf(code)
        );
        if (haveException) {
            throw exception;
        }
        return result;
    }


    /**
     * 过滤参数
     *
     * @param args 排除与 HttpServletRequest HttpServletResponse MultipartFilter 有关的参数
     * @return 符合要求的参数集合
     */
    private List<Object> filterArgs(Object[] args) {
        return Arrays.stream(args).filter(object -> !(object instanceof MultipartFilter)
                && !(object instanceof HttpServletRequest)
                && !(object instanceof HttpServletResponse)
        ).collect(Collectors.toList());
    }
}
