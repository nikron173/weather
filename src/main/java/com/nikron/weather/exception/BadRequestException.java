package com.nikron.weather.exception;

public class BadRequestException extends ApplicationException {
    public BadRequestException(String error, int code) {
        super(error, code);
    }
}
