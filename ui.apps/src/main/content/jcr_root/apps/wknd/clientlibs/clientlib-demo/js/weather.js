    const weatherBtn = document.getElementById("btn-show-weather");
    const container = document.getElementById("weather-container");

    if (weatherBtn && container) {
      weatherBtn.addEventListener("click", function () {
        container.innerHTML = "<em>Loading weather data...</em>";

        fetch("/bin/wknd/weather")
          .then(function (response) {
            if (!response.ok) {
              throw new Error("Network response was not ok");
            }
            return response.text();
          })
          .then(function (html) {
            container.innerHTML = html;
          })
          .catch(function (error) {
            container.innerHTML =
              "<span style='color: red;'>Failed to load weather.</span>";
          });
      });
    }
