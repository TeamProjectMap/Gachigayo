// src/main/resources/static/js/weather.js 에 들어갈 내용
$(document).ready(function() {
    loadWeatherData();
});

function loadWeatherData() {
    const lat = "37.5665";
    const lon = "126.9780";

    $.ajax({
        url: '/weather/getWeather',
        type: 'GET',
        data: { lat: lat, lon: lon },
        dataType: 'json',
        success: function(response) {
            const temp = Math.round(response.currentTemp);
            const weatherMain = response.currentWeather;
            const iconCode = response.currentIcon;

            $('#temperature').text(temp + '°C');

            let mainText = '오늘의 날씨';
            let subText = '좋은 하루 보내세요';

            if (weatherMain === 'Rain' || weatherMain === 'Drizzle') {
                mainText = '오늘은 비가 와요';
                subText = '나갈 때 우산을 챙기세요';
            } else if (weatherMain === 'Clear') {
                mainText = '오늘은 맑아요';
                subText = '외출하기 좋은 날씨예요';
            } else if (weatherMain === 'Clouds') {
                mainText = '오늘은 구름이 많아요';
                subText = '외출 시 참고해주세요';
            }

            $('#weatherMainText').text(mainText);
            $('#weatherSubText').text(subText);

            if (iconCode) {
                const iconUrl = 'https://openweathermap.org/img/wn/' + iconCode + '@2x.png';
                $('#weatherIcon').attr('src', iconUrl).show();
            }
        },
        error: function(xhr, status, error) {
            console.error("날씨 연동 오류:", error);
            $('#weatherMainText').text('날씨 정보를 가져올 수 없어요');
            $('#temperature').text('-');
        }
    });
}