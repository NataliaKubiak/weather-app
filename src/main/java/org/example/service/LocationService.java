package org.example.service;

import org.example.entities.Location;
import org.example.entities.User;
import org.example.entities.dto.LocationResponseDto;
import org.example.entities.dto.RemoveLocationDto;
import org.example.entities.dto.UserDto;
import org.example.entities.mappers.UserDtoToUserMapper;
import org.example.repository.LocationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocationService {

    private final LocationDao locationDao;

    private final UserDtoToUserMapper userDtoToUserMapper = UserDtoToUserMapper.INSTANCE;

    @Autowired
    public LocationService(LocationDao locationDao) {
        this.locationDao = locationDao;
    }

    @Transactional
    public Location saveLocationForUser(LocationResponseDto locationResponseDto, UserDto userDto) {
        User user = userDtoToUserMapper.toEntity(userDto);

        // TODO: 23/01/2025 можно ли написать маппер вместо ручного маппинга как тут?
        Location location = Location.builder()
                .name(locationResponseDto.getName())
                .user(user)
                .latitude(locationResponseDto.getLatitude())
                .longitude(locationResponseDto.getLongitude())
                .build();

        locationDao.createLocation(location);
        return location;
    }

    @Transactional
    public void removeLocationForUser(RemoveLocationDto removeLocationDto, UserDto userDto) {
        locationDao.deleteLocationByNameForUser(
                removeLocationDto.getName(),
                userDto.getId()
        );

        locationDao.deleteLocationByCoordinatesForUser(
                removeLocationDto.getLongitude(),
                removeLocationDto.getLatitude(), userDto.getId()
        );
    }
}
