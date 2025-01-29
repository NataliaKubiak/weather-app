package org.example.entities.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationResponseDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("country")
    private String country;

    @JsonProperty("lon")
    private double longitude;

    @JsonProperty("lat")
    private double latitude;

    @JsonProperty("coord")
    private void unpackCoord(Map<String, Double> coord) {
        this.longitude = coord.get("lon");
        this.latitude = coord.get("lat");
    }

    @JsonProperty("sys")
    private void unpackSys(Map<String, Object> sys) {
        this.country = (String) sys.get("country");
    }
}
