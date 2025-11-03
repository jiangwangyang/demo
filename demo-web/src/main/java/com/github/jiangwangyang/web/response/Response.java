package com.github.jiangwangyang.web.response;

import lombok.Data;

import java.util.Map;

@Data
public class Response<T> {

    private Boolean success;
    private String message;
    private T data;
    private Map<String, Object> extra;

    public static <T> Response<T> success() {
        Response<T> response = new Response<>();
        response.setSuccess(true);
        response.setMessage("success");
        return response;
    }

    public static <T> Response<T> success(T data) {
        Response<T> response = success();
        response.setData(data);
        return response;
    }

    public static <T> Response<T> success(T data, Map<String, Object> extra) {
        Response<T> response = success(data);
        response.setExtra(extra);
        return response;
    }


    public static <T> Response<T> fail(String message) {
        Response<T> response = new Response<>();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    public static <T> Response<T> fail(String message, Map<String, Object> extra) {
        Response<T> response = fail(message);
        response.setExtra(extra);
        return response;
    }
}
