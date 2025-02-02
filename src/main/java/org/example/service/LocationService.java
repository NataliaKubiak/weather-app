package org.example.service;

import lombok.extern.log4j.Log4j2;
import org.example.entities.Location;
import org.example.entities.User;
import org.example.entities.dto.LocationResponseDto;
import org.example.entities.dto.RemoveLocationDto;
import org.example.entities.dto.UserDto;
import org.example.entities.mappers.UserDtoToUserMapper;
import org.example.exceptions.EntityAlreadyExistsException;
import org.example.repository.LocationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
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

        Location location = Location.builder()
                .name(locationResponseDto.getName() + ", " + locationResponseDto.getCountry())
                .user(user)
                .latitude(locationResponseDto.getLatitude())
                .longitude(locationResponseDto.getLongitude())
                .build();

        try {
            locationDao.createLocation(location);

        } catch (EntityAlreadyExistsException ex) {
            log.info("Location '" + location.getName() + "' for user '" + location.getUser().getLogin() + "' already exists");
        }

        return location;
    }

    @Transactional
    public void removeLocationForUser(RemoveLocationDto removeLocationDto, UserDto userDto) {
        locationDao.deleteLocationByNameForUser(
                removeLocationDto.getName(),
                userDto.getId()
        );
    }
}
