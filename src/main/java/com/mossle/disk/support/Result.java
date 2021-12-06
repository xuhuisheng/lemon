package com.mossle.disk.support;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Result<T> {
    public static final Result SUCCESS = new Result();
    private int code = 0;
    private String message = "success";
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    //
    @JsonIgnore
    public boolean isSuccess() {
        return this.code == 0;
    }

    @JsonIgnore
    public boolean isFailure() {
        return this.code != 0;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.setData(data);

        return result;
    }

    public static <T> Result<T> success() {
        return SUCCESS;
    }

    public static <T> Result<T> failure(int code, String message) {
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setMessage(message);

        return result;
    }

    public static <T> Result<T> failure(int code, String message, T data) {
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);

        return result;
    }
}
