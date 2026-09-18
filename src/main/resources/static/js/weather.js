const weatherForm = document.getElementById("weatherForm");
const cityInput = document.getElementById("cityInput");
const message = document.getElementById("message");
const weatherPanel = document.getElementById("weatherPanel");
const forecastPanel = document.getElementById("forecastPanel");

function setMessage(text) {
    message.textContent = text || "";
}

function renderForecast(forecast) {
    forecastPanel.innerHTML = "";

    forecast.forEach((day) => {
        const card = document.createElement("article");
        card.className = "forecast-card";

        card.innerHTML = `
            <h3>${day.date}</h3>
            <p>${day.weatherSummary}</p>
            <p>High: ${day.temperatureMax.toFixed(1)}°C</p>
            <p>Low: ${day.temperatureMin.toFixed(1)}°C</p>
        `;

        forecastPanel.appendChild(card);
    });
}

function renderWeather(data) {
    weatherPanel.classList.remove("hidden");

    document.getElementById("locationName").textContent = `${data.locationName}, ${data.country}`;
    document.getElementById("summary").textContent = `${data.weatherSummary} (code ${data.weatherCode})`;
    document.getElementById("temperature").textContent = `${data.temperature.toFixed(1)}°C`;
    document.getElementById("apparentTemperature").textContent = `${data.apparentTemperature.toFixed(1)}°C`;
    document.getElementById("humidity").textContent = `${data.humidity}%`;
    document.getElementById("windSpeed").textContent = `${data.windSpeed.toFixed(1)} km/h`;

    renderForecast(data.forecast || []);
}

weatherForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const city = cityInput.value.trim();
    if (!city) {
        setMessage("Please enter a city name.");
        return;
    }

    setMessage("Loading weather...");

    try {
        const response = await fetch(`/api/weather?city=${encodeURIComponent(city)}`);
        const payload = await response.json();

        if (!response.ok) {
            weatherPanel.classList.add("hidden");
            forecastPanel.innerHTML = "";
            setMessage(payload.message || "Could not load weather right now.");
            return;
        }

        renderWeather(payload);
        setMessage("");
    } catch (error) {
        weatherPanel.classList.add("hidden");
        forecastPanel.innerHTML = "";
        setMessage("Network error while loading weather data. Please try again.");
    }
});
