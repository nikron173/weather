package com.nikron.weather.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WeatherApiException extends RuntimeException {
    private final String error;
    private final int code;
}
