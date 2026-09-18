# roommate-application

Spring Boot MVC + JSP roommate application starter with a working weather dashboard powered by Open-Meteo.

## Prerequisites

- Java 17+
- Maven 3.9+

## Weather API Used

This app uses public Open-Meteo endpoints with **no API key**:

- Geocoding API: `https://geocoding-api.open-meteo.com/v1/search`
- Forecast API: `https://api.open-meteo.com/v1/forecast`

## Configuration

Edit `/home/runner/work/roommate-application/roommate-application/src/main/resources/application.properties` if needed:

```properties
server.port=8080
spring.mvc.view.prefix=/WEB-INF/jsp/
spring.mvc.view.suffix=.jsp

weather.api.geocoding-url=https://geocoding-api.open-meteo.com/v1/search
weather.api.forecast-url=https://api.open-meteo.com/v1/forecast
weather.api.forecast-days=5
weather.api.timeout-ms=4000
```

## Run Locally

```bash
mvn clean test
mvn spring-boot:run
```

Open:

- Home: `http://localhost:8080/`
- Weather dashboard page: `http://localhost:8080/weather`
- Weather backend endpoint: `GET http://localhost:8080/api/weather?city=Ahmedabad`

## Features Implemented

- Home page with clear navigation button to weather dashboard
- City search with safe URL encoding
- Backend weather integration service (not exposed directly from JSP)
- Current weather display:
    - Summary from weather code
    - Temperature
    - Feels-like temperature
    - Humidity
    - Wind speed
- Multi-day forecast cards
- User-friendly error messages for:
    - Empty city input
    - City not found
    - API/network failures and timeout issues
- Timeouts and bounded input validation in backend

## Testing

Tests mock external API calls and do not rely on live Open-Meteo responses.

Run:

```bash
mvn test
```

## Troubleshooting

- **Port already in use**: change `server.port` in `application.properties`.
- **Weather request fails**: verify internet access from your environment.
- **City not found**: try a broader city name or correct spelling.
- **Build issues**: ensure Java 17+ and Maven are installed (`java -version`, `mvn -version`).
