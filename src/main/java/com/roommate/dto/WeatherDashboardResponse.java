package com.roommate.dto;

import java.util.List;

public record WeatherDashboardResponse(
        String locationName,
        String country,
        double latitude,
        double longitude,
        String weatherSummary,
        double temperature,
        double apparentTemperature,
        int humidity,
        double windSpeed,
        int weatherCode,
        List<DailyForecast> forecast
) {
}
