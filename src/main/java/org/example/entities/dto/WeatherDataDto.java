package org.example.entities.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherDataDto {

    @JsonProperty("name")
    private String name;

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

    @JsonProperty("sys")
    private void unpackSys(Map<String, Object> sys) {
        this.country = (String) sys.get("country");
    }

    @JsonProperty("weather")
    private void unpackWeather(List<Map<String, Object>> weatherList) {
        if (weatherList != null && !weatherList.isEmpty()) {
            Map<String, Object> weather = weatherList.get(0);
            this.description = (String) weather.get("description");
            this.icon = (String) weather.get("icon");
        }
    }

    @JsonProperty("main")
    private void unpackMain(Map<String, Object> main) {
        this.temp = (int) Math.round((double) main.get("temp"));
        this.feelsLike = (int) Math.round((double) main.get("feels_like"));
        this.humidity = (int) main.get("humidity");
    }
}
