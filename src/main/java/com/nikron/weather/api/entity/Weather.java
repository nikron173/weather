package com.nikron.weather.api.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nikron.weather.api.util.UnixWeatherDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@FieldDefaults(level = PRIVATE)
public class Weather {

    @JsonProperty("clouds")
    Cloud cloud;

    @JsonProperty("weather")
    WeatherState[] weatherState;

    @JsonProperty("main")
    Temperature temperature;

    Wind wind;

    @JsonProperty("dt")
    @JsonDeserialize(using = UnixWeatherDeserializer.class)
    LocalDateTime time;
}
