package com.nikron.weather.api.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nikron.weather.api.entity.Cloud;
import com.nikron.weather.api.entity.Temperature;
import com.nikron.weather.api.entity.Weather;
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
public class Forecast {

    @JsonProperty("clouds")
    Cloud cloud;

    Weather[] weather;

    @JsonProperty("main")
    Temperature temperature;

    Wind wind;
}
