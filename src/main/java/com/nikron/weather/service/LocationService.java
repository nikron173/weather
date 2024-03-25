package com.nikron.weather.service;

import com.nikron.weather.api.dto.LocationDto;
import com.nikron.weather.entity.Location;
import com.nikron.weather.exception.NotFoundResourceException;
import com.nikron.weather.mapper.LocationMapper;
import com.nikron.weather.repository.LocationRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class LocationService {
    private final LocationRepository locationRepository = LocationRepository.getInstance();
    private final LocationMapper locationMapper = LocationMapper.getInstance();

    private final static LocationService INSTANCE = new LocationService();

    public static LocationService getInstance() {
        return INSTANCE;
    }

    public LocationDto find(Long id) {
        Optional<Location> location = locationRepository.find(id);
        if (location.isPresent()){
            return locationMapper.convertToDto(location.get());
        }
        throw new NotFoundResourceException(String.format("Location with ID \"%s\" not founded", id),
                HttpServletResponse.SC_NOT_FOUND);
    }

    public LocationDto find(String name, BigDecimal latitude, BigDecimal longitude) {
        Optional<Location> location = locationRepository.find(name, latitude, longitude);
        if (location.isPresent()){
            return locationMapper.convertToDto(location.get());
        }
        throw new NotFoundResourceException(String.format("Location with name \"%s\" not founded", name),
                HttpServletResponse.SC_NOT_FOUND);
    }

    public LocationDto find(BigDecimal latitude, BigDecimal longitude) {
        Optional<Location> location = locationRepository.find(latitude, longitude);
        if (location.isPresent()){
            return locationMapper.convertToDto(location.get());
        }
        throw new NotFoundResourceException(String.format("Location with (latitude,longitude) \"(%s,%s)\" not founded",
                latitude, longitude),
                HttpServletResponse.SC_NOT_FOUND);
    }

    public List<LocationDto> findAll() {
        return locationRepository.findAll().stream().map(locationMapper::convertToDto).toList();
    }

    public LocationDto save(LocationDto dto) {
        Optional<Location> location = locationRepository.find(dto.getLatitude(), dto.getLongitude());
        if (location.isEmpty()) {
            return locationMapper.convertToDto(locationRepository.save(locationMapper.convertToEntity(dto)));
        }
        return locationMapper.convertToDto(location.get());
    }
}
