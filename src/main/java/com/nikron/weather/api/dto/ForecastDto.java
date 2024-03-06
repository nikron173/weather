package com.nikron.weather.api.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@FieldDefaults(level = PRIVATE)
public class ForecastDto {
    String locationName;
    String locationCountry;
    String locationState;
    String latitude;
    String longitude;

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
}
