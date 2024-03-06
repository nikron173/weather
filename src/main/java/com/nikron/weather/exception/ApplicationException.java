package com.nikron.weather.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
    private final String error;
    private final int code;
    public ApplicationException(String error, int code){
        this.error = error;
        this.code = code;
    }
}
