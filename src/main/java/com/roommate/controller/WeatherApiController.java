package com.roommate.controller;

import java.util.Map;

import com.roommate.dto.WeatherDashboardResponse;
import com.roommate.service.WeatherException;
import com.roommate.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
public class WeatherApiController {

    private final WeatherService weatherService;

    public WeatherApiController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<?> getWeather(@RequestParam("city") String city) {
        try {
            WeatherDashboardResponse response = weatherService.getWeatherForCity(city);
            return ResponseEntity.ok(response);
        } catch (WeatherException ex) {
            return ResponseEntity.status(ex.getStatus())
                    .body(Map.of("message", ex.getMessage()));
        }
    }
}
