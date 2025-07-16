package com.example.eyo.data;

public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private String error;

    public ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public ApiResponse(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }
} 