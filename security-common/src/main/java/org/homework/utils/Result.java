package org.homework.utils;

import lombok.Data;

@Data
public class Result {

    private Integer code;
    /**
     * 结果状态 ,具体状态码参见枚举类ReturnCodeEnum.java
     */
    private String message;
    private Object data;

    public static Result success() {
        Result resultData = new Result();
        resultData.setCode(200);
        resultData.setMessage("success");
        return resultData;
    }

    public static Result success(Object data) {
        Result resultData = new Result();
        resultData.setCode(200);
        resultData.setMessage("success");
        resultData.setData(data);
        return resultData;
    }

    public static Result fail(String message, Integer code) {
        Result resultData = new Result();
        resultData.setCode(code);
        resultData.setMessage(message);
        return resultData;
    }

    public static Result fail(String message) {
        return fail(message, 500);
    }

}
