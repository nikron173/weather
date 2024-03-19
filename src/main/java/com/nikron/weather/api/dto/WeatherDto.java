package com.nikron.weather.api.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@FieldDefaults(level = PRIVATE)
public class WeatherDto {
    String temp;
    String tempMin;
    String tempMax;
    String feelsLike;
    String humidity;

    String windSpeed;
    String windDeg;
    String windGust;

    String cloud;

    String weatherState;
    String weatherDescription;

    String time;
}
