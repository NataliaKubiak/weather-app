package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.example.entities.Location;
import org.example.entities.User;
import org.example.entities.dto.LocationResponseDto;
import org.example.entities.dto.UserDto;
import org.example.entities.dto.WeatherDataDto;
import org.example.repository.LocationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class OpenWeatherService {

    private final RestTemplate restTemplate;
    private ObjectMapper mapper;

    private final LocationDao locationDao;

    @Value("${openweather.api.key}")
    private String apiKey;

    @Autowired
    public OpenWeatherService(RestTemplate restTemplate,
                              LocationDao locationDao) {
        this.restTemplate = restTemplate;
        this.locationDao = locationDao;
        this.mapper = new ObjectMapper();
    }

    public List<LocationResponseDto> getLocationByCity(String city) throws UnsupportedEncodingException, JsonProcessingException {
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);

        String url = UriComponentsBuilder.fromUriString("https://api.openweathermap.org/data/2.5/weather")
                .queryParam("q", encodedCity)
                .queryParam("units", "metric")
                .queryParam("appid", apiKey)
                .toUriString();

        log.info("Generated URL = {}", url);

        String jsonString = restTemplate.getForObject(url, String.class);

        return List.of(mapper.readValue(jsonString, LocationResponseDto.class));
    }

    @Transactional
    public List<WeatherDataDto> getWeatherForLocationsOf(UserDto userDto) throws JsonProcessingException {
        List<Location> locationsByUserId = locationDao.getLocationsByUserId(userDto.getId());
        List<WeatherDataDto> weatherDataList = new ArrayList<>();

        for(Location location : locationsByUserId) {

            String url = UriComponentsBuilder.fromUriString("https://api.openweathermap.org/data/2.5/weather")
                    .queryParam("lat", location.getLatitude())
                    .queryParam("lon", location.getLongitude())
                    .queryParam("units", "metric")
                    .queryParam("appid", apiKey)
                    .toUriString();

            log.info("Generated URL = {}", url);

            String jsonString = restTemplate.getForObject(url, String.class);
            WeatherDataDto weatherDataDto = mapper.readValue(jsonString, WeatherDataDto.class);

            weatherDataList.add(weatherDataDto);
        }

        return weatherDataList;
    }
}
