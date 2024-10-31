package org.homework.utils;

import lombok.AllArgsConstructor;

/**
 * @author yid
 */
@AllArgsConstructor
public class ResponseCode {
    /**
     * 成功
     */
    public static final Integer STATUS_SUCCESS = 200;
    public static final String SUCCESS_MSG = "操作成功";
    /**
     * 未登录
     */
    public static final Integer STATUS_NOT_LOGGED_IN = 401;
    public static final String NOT_LOGGED_IN_MSG = "未登录";
    /**
     * 权限不足
     */
    public static final Integer STATUS_PERMISSION_DENIED = 403;
    public static final String PERMISSION_DENIED_MSG = "权限不足";
    /**
     * 请求不存在
     */
    public static final Integer STATUS_PAGE_NOT_FOUND = 404;
    public static final String PAGE_NOT_FOUND_MSG = "请求不存在";
    /**
     * 未知错误，请联系管理员
     */
    public static final Integer STATUS_UNKNOWN_ERROR = 500;
    public static final String UNKNOWN_ERROR_MSG = "未知错误，请联系管理员";

    /**
     * 操作失败
     */
    public static final Integer STATUS_ERROR = 501;
    public static final String ERROR_MSG = "操作失败";
    public static final Integer STATUS_NO_CONTENT = 404;

    /**
     * 账号密码错误
     */
    public static final Integer STATUS_UNAUTHORIZED = 403;

    /**
     * 集合为空
     */

    public static final Integer STATUS_LIST_EMPTY = 505;
    public static final String MSG_LIST_EMPTY = "集合为空";

    public static final Integer STATUS_EMAIL_ERROR = 402;
    public static final String MSG_EMAIL_ERROR = "邮箱格式错误";


    /**
     * 验证码为空
     */
    public static final Integer STATUS_VERIFY_CODE_ERROR = 506;

    public static String getFailureMsg(Integer status) {
        String msg;
        switch (status) {
            case 401:
                msg = NOT_LOGGED_IN_MSG;
                break;
            case 403:
                msg = PERMISSION_DENIED_MSG;
                break;
            case 404:
                msg = PAGE_NOT_FOUND_MSG;
                break;
            case 500:
                msg = UNKNOWN_ERROR_MSG;
                break;
            case 501:
                msg = ERROR_MSG;
                break;
            default:
                msg = "系统繁忙";
        }
        return msg;
    }
}
