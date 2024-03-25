package com.nikron.weather.exception;

public class ResourceExistsException extends ApplicationException{
    public ResourceExistsException(String error, int code) {
        super(error, code);
    }
}
