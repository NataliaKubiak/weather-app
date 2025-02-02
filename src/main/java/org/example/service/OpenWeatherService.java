package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.example.exceptions.LocationNotFoundException;
import org.example.entities.Location;
import org.example.entities.dto.LocationResponseDto;
import org.example.entities.dto.UserDto;
import org.example.entities.dto.WeatherDataDto;
import org.example.repository.LocationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class OpenWeatherService {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

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
        log.info("Searching City with name: {}", city);
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);

        String url = UriComponentsBuilder.fromUriString("https://api.openweathermap.org/geo/1.0/direct")
                .queryParam("q", encodedCity)
                .queryParam("limit", "5")
                .queryParam("appid", apiKey)
                .toUriString();

        log.info("Generated URL = {}", url);

        String jsonString = "";
        try {
            jsonString = restTemplate.getForObject(url, String.class);

        } catch (RestClientException ex) {
            handleApiError(ex);
        }

        return mapper.readValue(jsonString, new TypeReference<List<LocationResponseDto>>(){});
    }

    @Transactional
    public Map<String, WeatherDataDto> getWeatherOfLocationsForUser(UserDto userDto) throws JsonProcessingException {
        log.info("Getting Weather for all locations for User: {}", userDto.getLogin());

        List<Location> locationsByUserId = locationDao.getLocationsByUserId(userDto.getId());
        Map<String, WeatherDataDto> weatherDataMap = new HashMap<>();

        for (Location location : locationsByUserId) {

            log.info("Getting Weather Info For the Location: {}", location.getName());
            String url = UriComponentsBuilder.fromUriString("https://api.openweathermap.org/data/2.5/weather")
                    .queryParam("lat", location.getLatitude())
                    .queryParam("lon", location.getLongitude())
                    .queryParam("units", "metric")
                    .queryParam("appid", apiKey)
                    .toUriString();

            log.info("Generated URL = {}", url);

            String jsonString = "";
            try {
                jsonString = restTemplate.getForObject(url, String.class);

            } catch (RestClientException ex) {
                handleApiError(ex);
            }

            WeatherDataDto weatherDataDto = mapper.readValue(jsonString, WeatherDataDto.class);

            weatherDataMap.put(location.getName(), weatherDataDto);
        }

        return weatherDataMap;
    }

    private void handleApiError(RestClientException exception) {
        if (exception instanceof HttpClientErrorException clientError) {
            if (clientError.getStatusCode().value() == 404) { // 404 - Not Found
                log.warn("Location not found: {}", clientError.getResponseBodyAsString());
                throw new LocationNotFoundException("Location not found");
            }
        }

        log.error("Unexpected error occurred while calling external API: {}", exception.getMessage());
        throw new ResourceAccessException("Unexpected error occurred while calling external API");
    }
}
