package org.example.entities.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherDataDto {

    @JsonProperty("country")
    private String country;

    @JsonProperty("description")
    private String description;

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("temp")
    private int temp;

    @JsonProperty("feels_like")
    private int feelsLike;

    @JsonProperty("humidity")
    private int humidity;

    @JsonProperty("lon")
    private double longitude;

    @JsonProperty("lat")
    private double latitude;

    @JsonProperty("sys")
    private void unpackSys(Map<String, Object> sys) {
        this.country = (String) sys.get("country");
    }

    @JsonProperty("coord")
    private void unpackCoord(Map<String, Double> coord) {
        this.longitude = coord.get("lon");
        this.latitude = coord.get("lat");
    }

    @JsonProperty("weather")
    private void unpackWeather(List<Map<String, Object>> weatherList) {
        if (weatherList != null && !weatherList.isEmpty()) {
            Map<String, Object> weather = weatherList.get(0);

            this.description = (String) weather.get("description");
            description = makeFirstLetterCapital(description);

            this.icon = (String) weather.get("icon");
        }
    }

    @JsonProperty("main")
    private void unpackMain(Map<String, Object> main) {
        this.temp = (int) Math.round(((Number) main.get("temp")).doubleValue());
        this.feelsLike = (int) Math.round(((Number) main.get("feels_like")).doubleValue());
        this.humidity = (int) main.get("humidity");
    }

    private String makeFirstLetterCapital(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase(Locale.ROOT);
    }
}
