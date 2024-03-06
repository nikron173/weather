package com.nikron.weather.exception;

public class NotFoundResourceException extends ApplicationException {
    public NotFoundResourceException(String error, int code) {
        super(error, code);
    }
}
