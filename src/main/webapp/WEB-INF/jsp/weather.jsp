<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Weather Dashboard</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<div class="page-shell">
    <header class="top-bar">
        <h1>Weather Dashboard</h1>
        <a href="/" class="secondary-link">← Home</a>
    </header>

    <section class="card search-card">
        <form id="weatherForm">
            <label for="cityInput">Search City</label>
            <div class="search-row">
                <input id="cityInput" name="city" type="text" maxlength="100" placeholder="e.g. Ahmedabad" required>
                <button class="primary-btn" type="submit">Get Weather</button>
            </div>
        </form>
        <p id="message" class="message" role="status" aria-live="polite"></p>
    </section>

    <section id="weatherPanel" class="card hidden">
        <h2 id="locationName"></h2>
        <p id="summary"></p>
        <div class="metrics-grid">
            <div class="metric"><span>Temperature</span><strong id="temperature"></strong></div>
            <div class="metric"><span>Feels Like</span><strong id="apparentTemperature"></strong></div>
            <div class="metric"><span>Humidity</span><strong id="humidity"></strong></div>
            <div class="metric"><span>Wind Speed</span><strong id="windSpeed"></strong></div>
        </div>
    </section>

    <section id="forecastPanel" class="forecast-grid"></section>
</div>
<script src="/js/weather.js"></script>
</body>
</html>
