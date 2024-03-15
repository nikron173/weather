package com.nikron.weather.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikron.weather.api.dto.WeatherDto;
import com.nikron.weather.api.entity.Forecast;
import com.nikron.weather.api.entity.Weather;
import com.nikron.weather.api.dto.LocationDto;
import com.nikron.weather.api.exception.WeatherApiException;
import com.nikron.weather.entity.Location;
import com.nikron.weather.util.EnvironmentVariable;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WeatherApi {

    private final String urlLocation = "https://api.openweathermap.org/geo/1.0/direct?q=%s&appid=%s&limit=5";
    private final String urlWeather = "https://api.openweathermap.org/data/2.5/weather?" +
            "lat=%f&lon=%f&appid=%s&units=metric";

    private final String urlForecast = "https://api.openweathermap.org/data/2.5/forecast?" +
            "lat=%s&lon=%s&appid=%s&units=metric";

    private final ObjectMapper mapper = new ObjectMapper();
    private final static WeatherApi INSTANCE = new WeatherApi();

    private WeatherApi() {
    }

    public static WeatherApi getInstance() {
        return INSTANCE;
    }

    public List<LocationDto> getLocation(String location) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(urlLocation, location, EnvironmentVariable.APP_ID)))
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new WeatherApiException(
                String.format("Service access error https://api.openweathermap.org. %s",
                        response.body().isBlank() ? "Try again later" :
                                mapper.readTree(response.body()).get("message").asText()),
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        );
        return mapper.readValue(response.body(), new TypeReference<>() {
        });
    }

    public List<WeatherDto> getForecast(Location location) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(urlForecast, location.getLatitude(),
                        location.getLongitude(), EnvironmentVariable.APP_ID)))
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Forecast forecast = mapper.readValue(response.body(), Forecast.class);
        return forecast.getWeathers().stream().map(weather -> WeatherDto.builder()
                .locationId(location.getId())
                .locationName(location.getName())
                .locationCountry(location.getCountry())
                .locationState(location.getState())
                .latitude(String.valueOf(location.getLatitude()))
                .longitude(String.valueOf(location.getLongitude()))
                .temp(String.valueOf(weather.getTemperature().getTemp()))
                .tempMin(String.valueOf(weather.getTemperature().getTempMin()))
                .tempMax(String.valueOf(weather.getTemperature().getTempMax()))
                .humidity(String.valueOf(weather.getTemperature().getHumidity()))
                .feelsLike(String.valueOf(weather.getTemperature().getFeelsLike()))
                .windDeg(String.valueOf(weather.getWind().getDeg()))
                .windSpeed(String.valueOf(weather.getWind().getSpeed()))
                .windGust(String.valueOf(weather.getWind().getGust()))
                .cloud(String.valueOf(weather.getCloud().getCloud()))
                .weatherState(weather.getWeatherState()[0].getState())
                .weatherDescription(weather.getWeatherState()[0].getDescription())
                .time(weather.getTime().format(DateTimeFormatter.ofPattern("dd/MM HH:mm:ss")))
                .build()).toList();
    };

    public WeatherDto getWeather(LocationDto dto) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(urlWeather,
                        dto.getLatitude(), dto.getLongitude(), EnvironmentVariable.APP_ID)))
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new WeatherApiException(
                String.format("Service access error https://api.openweathermap.org. %s",
                        response.body().isBlank() ? "Try again later" :
                                mapper.readTree(response.body()).get("message").asText()),
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        );
        Weather weather = mapper.readValue(response.body(), Weather.class);
        return WeatherDto.builder()
                .locationId(dto.getId())
                .locationName(dto.getName())
                .locationCountry(dto.getCountry())
                .locationState(dto.getState())
                .latitude(String.valueOf(dto.getLatitude()))
                .longitude(String.valueOf(dto.getLongitude()))
                .temp(String.valueOf(weather.getTemperature().getTemp()))
                .tempMin(String.valueOf(weather.getTemperature().getTempMin()))
                .tempMax(String.valueOf(weather.getTemperature().getTempMax()))
                .humidity(String.valueOf(weather.getTemperature().getHumidity()))
                .feelsLike(String.valueOf(weather.getTemperature().getFeelsLike()))
                .windDeg(String.valueOf(weather.getWind().getDeg()))
                .windSpeed(String.valueOf(weather.getWind().getSpeed()))
                .windGust(String.valueOf(weather.getWind().getGust()))
                .cloud(String.valueOf(weather.getCloud().getCloud()))
                .weatherState(weather.getWeatherState()[0].getState())
                .weatherDescription(weather.getWeatherState()[0].getDescription())
                .time(weather.getTime().format(DateTimeFormatter.ofPattern("hh:mm:ss")))
                .build();
    }
}
