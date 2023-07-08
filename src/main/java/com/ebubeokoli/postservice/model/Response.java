package com.ebubeokoli.postservice.model;

public class Response<T> {
    
    private boolean isSuccess;

    public boolean isSuccess() {
        return isSuccess;
    }

    private T data;

    public T getData() {
        return data;
    }

    private String message;

    public String getMessage() {
        return message;
    }

    public Response(boolean isSuccess, T data, String message) {
        this.isSuccess = isSuccess;
        this.data = data;
        this.message = message;
    }
}
