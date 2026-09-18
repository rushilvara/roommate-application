package com.roommate.controller;

import com.roommate.dto.WeatherDashboardResponse;
import com.roommate.service.WeatherException;
import com.roommate.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherApiController.class)
class WeatherApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @Test
    void getWeatherReturnsOkWhenServiceSucceeds() throws Exception {
        WeatherDashboardResponse response = new WeatherDashboardResponse(
                "Ahmedabad", "India", 23.03, 72.58,
                "Clear sky", 30.0, 31.0, 60, 10.5, 0,
                List.of()
        );

        when(weatherService.getWeatherForCity("Ahmedabad")).thenReturn(response);

        mockMvc.perform(get("/api/weather").param("city", "Ahmedabad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locationName").value("Ahmedabad"))
                .andExpect(jsonPath("$.weatherSummary").value("Clear sky"));
    }

    @Test
    void getWeatherReturnsErrorStatusFromWeatherException() throws Exception {
        when(weatherService.getWeatherForCity("Unknown"))
                .thenThrow(new WeatherException("No location found", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/weather").param("city", "Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No location found"));
    }
}
