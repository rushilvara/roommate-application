package com.roommate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roommate.dto.WeatherDashboardResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getWeatherForCityReturnsExpectedDashboardData() {
        WeatherService service = new WeatherService(
                restTemplate,
                objectMapper,
                "https://geocoding-api.open-meteo.com/v1/search",
                "https://api.open-meteo.com/v1/forecast",
                5
        );

        String geocodeJson = """
                {
                  "results": [
                    {"name": "Ahmedabad", "country": "India", "latitude": 23.03, "longitude": 72.58}
                  ]
                }
                """;

        String forecastJson = """
                {
                  "current": {
                    "temperature_2m": 31.2,
                    "apparent_temperature": 34.8,
                    "relative_humidity_2m": 62,
                    "wind_speed_10m": 14.4,
                    "weather_code": 2
                  },
                  "daily": {
                    "time": ["2026-09-18", "2026-09-19"],
                    "weather_code": [2, 61],
                    "temperature_2m_max": [33.4, 32.0],
                    "temperature_2m_min": [26.1, 25.3]
                  }
                }
                """;

        when(restTemplate.getForObject(any(URI.class), eq(String.class))).thenReturn(geocodeJson, forecastJson);

        WeatherDashboardResponse response = service.getWeatherForCity("Ahmedabad");

        assertEquals("Ahmedabad", response.locationName());
        assertEquals("India", response.country());
        assertEquals("Partly cloudy", response.weatherSummary());
        assertEquals(31.2, response.temperature());
        assertEquals(2, response.forecast().size());
        assertEquals("Slight rain", response.forecast().get(1).weatherSummary());
    }

    @Test
    void getWeatherForCityThrowsBadRequestWhenCityBlank() {
        WeatherService service = new WeatherService(
                restTemplate,
                objectMapper,
                "https://geocoding-api.open-meteo.com/v1/search",
                "https://api.open-meteo.com/v1/forecast",
                5
        );

        WeatherException exception = assertThrows(WeatherException.class, () -> service.getWeatherForCity("   "));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void getWeatherForCityThrowsNotFoundWhenNoResults() {
        WeatherService service = new WeatherService(
                restTemplate,
                objectMapper,
                "https://geocoding-api.open-meteo.com/v1/search",
                "https://api.open-meteo.com/v1/forecast",
                5
        );

        when(restTemplate.getForObject(any(URI.class), eq(String.class)))
                .thenReturn("{\"results\":[]}");

        WeatherException exception = assertThrows(WeatherException.class, () -> service.getWeatherForCity("Nowhere"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void getWeatherForCityThrowsBadRequestForUnsupportedCharacters() {
        WeatherService service = new WeatherService(
                restTemplate,
                objectMapper,
                "https://geocoding-api.open-meteo.com/v1/search",
                "https://api.open-meteo.com/v1/forecast",
                5
        );

        WeatherException exception = assertThrows(WeatherException.class,
                () -> service.getWeatherForCity("London<script>"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void getWeatherForCityThrowsBadGatewayWhenApiFails() {
        WeatherService service = new WeatherService(
                restTemplate,
                objectMapper,
                "https://geocoding-api.open-meteo.com/v1/search",
                "https://api.open-meteo.com/v1/forecast",
                5
        );

        when(restTemplate.getForObject(any(URI.class), eq(String.class)))
                .thenThrow(new RestClientException("API unavailable"));

        WeatherException exception = assertThrows(WeatherException.class, () -> service.getWeatherForCity("London"));

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatus());
    }
}
