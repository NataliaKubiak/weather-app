package org.example.service;

import org.example.config.TestConfig;
import org.example.entities.dto.LocationResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringJUnitConfig(classes = TestConfig.class)
@WebAppConfiguration
@ActiveProfiles("test")
@Transactional
class OpenWeatherServiceTest {

    @Autowired
    private OpenWeatherService openWeatherService;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void testGetLocationByCity_Successful() throws Exception {
        String mockResponse = """
                {
                  "coord": {
                    "lon": -1.111,
                    "lat": 1.111
                  },
                  "sys": {
                    "country": "RU",
                    "sunrise": 17
                  },
                  "name": "Test",
                  "cod": 200
                }""";

        mockServer.expect(requestTo(containsString("/data/2.5/weather?q=Test")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        List<LocationResponseDto> testCityList = openWeatherService.getLocationByCity("Test City");

        assertNotNull(testCityList);

        assertEquals("Test", testCityList.get(0).getName());
        assertEquals(-1.111, testCityList.get(0).getLongitude());
        assertEquals(1.111, testCityList.get(0).getLatitude());
        assertEquals("RU", testCityList.get(0).getCountry());
    }


}