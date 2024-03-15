package com.nikron.weather.mapper;

import com.nikron.weather.api.dto.LocationDto;
import com.nikron.weather.entity.Location;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class LocationMapper implements Mapper<Location, LocationDto> {

    private static final LocationMapper INSTANCE = new LocationMapper();

    public static LocationMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public LocationDto convertToDto(Location entity) {
        return LocationDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .country(entity.getCountry())
                .state(entity.getState())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .build();
    }

    @Override
    public Location convertToEntity(LocationDto dto) {
        return Location.builder()
                .name(dto.getName())
                .country(dto.getCountry())
                .state(dto.getState())
                .longitude(dto.getLongitude())
                .latitude(dto.getLatitude())
                .build();
    }
}
