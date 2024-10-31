package org.homework.conventioin.result;

import lombok.Data;

@Data
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

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

    public static Result fail(String message, Integer code, Object data) {
        Result resultData = new Result();
        resultData.setData(data);
        resultData.setCode(code);
        resultData.setMessage(message);
        return resultData;
    }

    public static Result fail(String message) {
        return fail(message, 500, null);
    }

    public static Result fail(String message, Object data) {
        return fail(message, 500, data);
    }

}
