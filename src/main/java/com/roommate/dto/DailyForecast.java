package com.roommate.dto;

public record DailyForecast(
        String date,
        int weatherCode,
        String weatherSummary,
        double temperatureMax,
        double temperatureMin
) {
}
