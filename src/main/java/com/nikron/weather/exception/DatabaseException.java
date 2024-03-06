package com.nikron.weather.exception;

public class DatabaseException extends ApplicationException {
    public DatabaseException(String error, int code) {
        super(error, code);
    }
}
