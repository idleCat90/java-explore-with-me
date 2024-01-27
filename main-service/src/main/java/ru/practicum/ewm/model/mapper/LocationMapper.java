package ru.practicum.ewm.model.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.model.Location;

@UtilityClass
public class LocationMapper {
    public static Location toLocation(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }

    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
