package org.homework.exceptionhandler;

import lombok.extern.slf4j.Slf4j;
import org.homework.utils.ResponseCode;
import org.homework.utils.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {


    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("报错信息：", e);
        return Result.fail("服务器错误，如有需要请发送错误日志", ResponseCode.STATUS_UNKNOWN_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result handleAccessDeniedException(AccessDeniedException e) {
        log.error("权限不足：", e);
        return Result.fail("权限不足，请联系管理员", 403);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("参数错误：", e);
        return Result.fail(e.getMessage());
    }

    /**
     * 捕获 @Valid @Validated 校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder allErrors = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            allErrors.append(error.getDefaultMessage());
        }
        String errorMessage = allErrors.toString();
        log.error(ex.getMessage());
        return Result.fail(errorMessage);
    }
}
