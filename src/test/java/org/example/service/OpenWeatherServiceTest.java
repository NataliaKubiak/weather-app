package org.example.service;

import config.TestConfig;
import org.example.entities.Location;
import org.example.entities.User;
import org.example.entities.dto.LocationResponseDto;
import org.example.exceptions.LocationNotFoundException;
import org.example.repository.LocationDao;
import org.example.repository.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringJUnitConfig(classes = TestConfig.class)
@WebAppConfiguration
@ActiveProfiles("test")
@Transactional
class OpenWeatherServiceTest {

    @Autowired
    private OpenWeatherService openWeatherService;

    @Autowired
    private LocationDao locationDao;

    @Autowired
    private UserDao userDao;

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
                [
                  {
                    "name": "TestCity",
                    "lat": 48.1,
                    "lon": 2.11,
                    "country": "RU",
                    "state": "TestState"
                  },
                  {
                    "name": "TestCity",
                    "lat": 33.1,
                    "lon": -95.1,
                    "country": "US",
                    "state": "TestState2"
                  }
                ]
                """;

        mockServer.expect(requestTo(containsString("/geo/1.0/direct?q=TestCity")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        List<LocationResponseDto> testCityList = openWeatherService.getLocationByCity("TestCity");

        assertEquals(2, testCityList.size());

        //first TestCity data
        assertEquals("TestCity", testCityList.get(0).getName());
        assertEquals(48.1, testCityList.get(0).getLatitude());
        assertEquals(2.11, testCityList.get(0).getLongitude());
        assertEquals("RU", testCityList.get(0).getCountry());
        assertEquals("TestState", testCityList.get(0).getState());
        //second TestCity data
        assertEquals("TestCity", testCityList.get(1).getName());
        assertEquals(33.1, testCityList.get(1).getLatitude());
        assertEquals(-95.1, testCityList.get(1).getLongitude());
        assertEquals("US", testCityList.get(1).getCountry());
        assertEquals("TestState2", testCityList.get(1).getState());
    }

    @Test
    void testGetLocationByCity_CityNotFound() {
        mockServer.expect(requestTo(containsString("/geo/1.0/direct?q=TestCity")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(LocationNotFoundException.class, () -> openWeatherService.getLocationByCity("TestCity"));
    }

    @Test
    void testGetLocationByCity_OpenWeatherApiNotRespond() {
        mockServer.expect(requestTo(containsString("/geo/1.0/direct?q=TestCity")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(ResourceAccessException.class, () -> openWeatherService.getLocationByCity("TestCity"));
    }

    @Test
    void getWeatherOfLocationsForUser_Successful() {
        User user = User.builder()
                .login("user")
                .encryptedPassword("1234")
                .build();

        userDao.createUser(user);

        Location location = Location.builder()
                .name("Test City")
                .user(user)
                .longitude(1.111)
                .latitude(2.22)
                .build();

        locationDao.createLocation(location);
        assertEquals("Test City", location.getName());
        assertEquals(1.111, location.getLongitude());
        assertEquals(2.22, location.getLatitude());
    }
}