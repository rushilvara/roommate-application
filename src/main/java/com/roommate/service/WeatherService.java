package com.roommate.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roommate.dto.DailyForecast;
import com.roommate.dto.WeatherDashboardResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WeatherService {

    private static final Map<Integer, String> WEATHER_CODE_DESCRIPTIONS = createWeatherCodeMap();

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String geocodingUrl;
    private final String forecastUrl;
    private final int forecastDays;

    public WeatherService(RestTemplate restTemplate,
                          ObjectMapper objectMapper,
                          @Value("${weather.api.geocoding-url}") String geocodingUrl,
                          @Value("${weather.api.forecast-url}") String forecastUrl,
                          @Value("${weather.api.forecast-days:5}") int forecastDays) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.geocodingUrl = geocodingUrl;
        this.forecastUrl = forecastUrl;
        this.forecastDays = Math.min(Math.max(forecastDays, 1), 10);
    }

    public WeatherDashboardResponse getWeatherForCity(String rawCity) {
        String city = validateCity(rawCity);

        JsonNode firstResult = fetchGeocodingResult(city);
        double latitude = firstResult.path("latitude").asDouble();
        double longitude = firstResult.path("longitude").asDouble();
        String locationName = firstResult.path("name").asText(city);
        String country = firstResult.path("country").asText("Unknown");

        JsonNode weatherData = fetchForecast(latitude, longitude);
        JsonNode current = weatherData.path("current");
        JsonNode daily = weatherData.path("daily");

        int weatherCode = current.path("weather_code").asInt();
        List<DailyForecast> forecasts = buildForecastList(daily);

        return new WeatherDashboardResponse(
                locationName,
                country,
                latitude,
                longitude,
                describeWeather(weatherCode),
                current.path("temperature_2m").asDouble(),
                current.path("apparent_temperature").asDouble(),
                current.path("relative_humidity_2m").asInt(),
                current.path("wind_speed_10m").asDouble(),
                weatherCode,
                forecasts
        );
    }

    private String validateCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new WeatherException("Please enter a city name.", HttpStatus.BAD_REQUEST);
        }

        String trimmed = city.trim();
        if (trimmed.length() > 100) {
            throw new WeatherException("City name is too long.", HttpStatus.BAD_REQUEST);
        }

        return trimmed;
    }

    private JsonNode fetchGeocodingResult(String city) {
        String url = UriComponentsBuilder.fromHttpUrl(geocodingUrl)
                .queryParam("name", city)
                .queryParam("count", 1)
                .queryParam("language", "en")
                .queryParam("format", "json")
                .build()
                .toUriString();

        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse == null ? "{}" : jsonResponse);
            JsonNode results = root.path("results");
            if (!results.isArray() || results.isEmpty()) {
                throw new WeatherException("No location found for that city. Try another search.", HttpStatus.NOT_FOUND);
            }
            return results.get(0);
        } catch (WeatherException ex) {
            throw ex;
        } catch (ResourceAccessException ex) {
            throw new WeatherException("Weather service timeout. Please try again.", HttpStatus.GATEWAY_TIMEOUT);
        } catch (RestClientException ex) {
            throw new WeatherException("Could not contact weather service. Please try again later.", HttpStatus.BAD_GATEWAY);
        } catch (Exception ex) {
            throw new WeatherException("Received invalid weather data. Please try again.", HttpStatus.BAD_GATEWAY);
        }
    }

    private JsonNode fetchForecast(double latitude, double longitude) {
        String url = UriComponentsBuilder.fromHttpUrl(forecastUrl)
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("current", "temperature_2m,apparent_temperature,relative_humidity_2m,wind_speed_10m,weather_code")
                .queryParam("daily", "weather_code,temperature_2m_max,temperature_2m_min")
                .queryParam("timezone", "auto")
                .queryParam("forecast_days", forecastDays)
                .build()
                .toUriString();

        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse == null ? "{}" : jsonResponse);
            if (!root.has("current") || !root.has("daily")) {
                throw new WeatherException("Weather forecast data is incomplete.", HttpStatus.BAD_GATEWAY);
            }
            return root;
        } catch (WeatherException ex) {
            throw ex;
        } catch (ResourceAccessException ex) {
            throw new WeatherException("Weather service timeout. Please try again.", HttpStatus.GATEWAY_TIMEOUT);
        } catch (RestClientException ex) {
            throw new WeatherException("Could not fetch weather forecast. Please try again later.", HttpStatus.BAD_GATEWAY);
        } catch (Exception ex) {
            throw new WeatherException("Received invalid weather forecast data.", HttpStatus.BAD_GATEWAY);
        }
    }

    private List<DailyForecast> buildForecastList(JsonNode daily) {
        List<DailyForecast> forecastList = new ArrayList<>();
        JsonNode dates = daily.path("time");
        JsonNode codes = daily.path("weather_code");
        JsonNode maxTemps = daily.path("temperature_2m_max");
        JsonNode minTemps = daily.path("temperature_2m_min");

        int size = Math.min(Math.min(dates.size(), codes.size()), Math.min(maxTemps.size(), minTemps.size()));

        for (int i = 0; i < size; i++) {
            int code = codes.get(i).asInt();
            forecastList.add(new DailyForecast(
                    dates.get(i).asText(),
                    code,
                    describeWeather(code),
                    maxTemps.get(i).asDouble(),
                    minTemps.get(i).asDouble()
            ));
        }
        return forecastList;
    }

    private String describeWeather(int code) {
        return WEATHER_CODE_DESCRIPTIONS.getOrDefault(code, "Unknown conditions");
    }

    private static Map<Integer, String> createWeatherCodeMap() {
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "Clear sky");
        map.put(1, "Mainly clear");
        map.put(2, "Partly cloudy");
        map.put(3, "Overcast");
        map.put(45, "Fog");
        map.put(48, "Depositing rime fog");
        map.put(51, "Light drizzle");
        map.put(53, "Moderate drizzle");
        map.put(55, "Dense drizzle");
        map.put(56, "Light freezing drizzle");
        map.put(57, "Dense freezing drizzle");
        map.put(61, "Slight rain");
        map.put(63, "Moderate rain");
        map.put(65, "Heavy rain");
        map.put(66, "Light freezing rain");
        map.put(67, "Heavy freezing rain");
        map.put(71, "Slight snow fall");
        map.put(73, "Moderate snow fall");
        map.put(75, "Heavy snow fall");
        map.put(77, "Snow grains");
        map.put(80, "Slight rain showers");
        map.put(81, "Moderate rain showers");
        map.put(82, "Violent rain showers");
        map.put(85, "Slight snow showers");
        map.put(86, "Heavy snow showers");
        map.put(95, "Thunderstorm");
        map.put(96, "Thunderstorm with slight hail");
        map.put(99, "Thunderstorm with heavy hail");
        return map;
    }
}
