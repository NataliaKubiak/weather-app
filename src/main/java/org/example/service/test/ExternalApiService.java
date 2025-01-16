package org.example.service.test;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Log4j2
@Service
public class ExternalApiService {

    private final RestTemplate restTemplate;

    @Value("${openweather.api.key}")
    private String apiKey;

    @Autowired
    public ExternalApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getWeatherByCity(String city) throws UnsupportedEncodingException {
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString());

        String url = UriComponentsBuilder.fromUriString("https://api.openweathermap.org/data/2.5/weather")
                .queryParam("q", encodedCity)
                .queryParam("units", "metric")
                .queryParam("appid", apiKey)
                .toUriString();

        log.info("Generated URL = {}", url);
        return restTemplate.getForObject(url, String.class);
    }
}
