package com.example.changshademo.common;

import java.io.Serializable;

public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private Integer code;
    private T data;

    // 构造函数
    public Result() {
    }

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Result(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // 静态工厂方法
    public static <T> Result<T> success(T data, String message) {
        Result<T> r = new Result<>(true, message, data);
        r.setCode(200);
        return r;
    }

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>(true, "操作成功", data);
        r.setCode(200);
        return r;
    }

    public static <T> Result<T> success(String message) {
        Result<T> r = new Result<>(true, message);
        r.setCode(200);
        return r;
    }

    public static <T> Result<T> error(String message) {
        Result<T> r = new Result<>(false, message);
        r.setCode(500);
        return r;
    }

    // Getter和Setter
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}