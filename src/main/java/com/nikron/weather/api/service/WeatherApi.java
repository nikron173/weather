package com.nikron.weather.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikron.weather.api.dto.ForecastDto;
import com.nikron.weather.api.entity.Forecast;
import com.nikron.weather.api.dto.LocationDto;
import com.nikron.weather.util.EnvironmentVariable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class WeatherApi {

    private final String urlFindLocation = "https://api.openweathermap.org/geo/1.0/direct?q=%s&appid=%s&limit=5";
    private final String urlGetForecast = "https://api.openweathermap.org/data/2.5/weather?" +
            "lat=%f&lon=%f&appid=%s&units=metric";

    private final ObjectMapper mapper = new ObjectMapper();
    private final static WeatherApi INSTANCE = new WeatherApi();

    private WeatherApi(){}

    public static WeatherApi getInstance() {
        return INSTANCE;
    }

    public List<LocationDto> getLocation(String location) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(urlFindLocation, location, EnvironmentVariable.APP_ID)))
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<>(){});
    }

    public ForecastDto getForecast(LocationDto dto) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(urlGetForecast,
                        dto.getLatitude(), dto.getLongitude(), EnvironmentVariable.APP_ID)))
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Forecast forecast = mapper.readValue(response.body(), Forecast.class);
        return ForecastDto.builder()
                .locationName(dto.getName())
                .locationCountry(dto.getCountry())
                .locationState(dto.getState())
                .latitude(String.valueOf(dto.getLatitude()))
                .longitude(String.valueOf(dto.getLongitude()))
                .temp(String.valueOf(forecast.getTemperature().getTemp()))
                .tempMin(String.valueOf(forecast.getTemperature().getTempMin()))
                .tempMax(String.valueOf(forecast.getTemperature().getTempMax()))
                .humidity(String.valueOf(forecast.getTemperature().getHumidity()))
                .feelsLike(String.valueOf(forecast.getTemperature().getFeelsLike()))
                .windDeg(String.valueOf(forecast.getWind().getDeg()))
                .windSpeed(String.valueOf(forecast.getWind().getSpeed()))
                .windGust(String.valueOf(forecast.getWind().getGust()))
                .cloud(String.valueOf(forecast.getCloud().getCloud()))
                .weatherState(forecast.getWeather()[0].getState())
                .weatherDescription(forecast.getWeather()[0].getDescription())
                .build();
    }
}
