package org.example.service;

import org.example.entities.Location;
import org.example.entities.User;
import org.example.entities.dto.LocationResponseDto;
import org.example.repository.LocationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocationService {

    private final LocationDao locationDao;

    @Autowired
    public LocationService(LocationDao locationDao) {
        this.locationDao = locationDao;
    }

    @Transactional
    public Location saveLocationForUser(LocationResponseDto locationResponseDto, User user) {
        // TODO: 23/01/2025 можно ли написать маппер вместо ручного маппинга как тут?
        Location location = Location.builder()
                .name(locationResponseDto.getName())
                .user(user)
                .latitude(locationResponseDto.getLatitude())
                .longitude(locationResponseDto.getLongitude())
                .build();

        // TODO: 23/01/2025 если у этого юзера уже есть локация с такими координатами или именем?

        locationDao.createLocation(location);
        return location;
    }

    @Transactional
    public void removeLocationForUser(String locationName, User user) {
        locationDao.deleteLocationForUser(locationName, user.getId());
    }
}
