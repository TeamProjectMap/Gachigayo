package com.baegopa.onestep.service;

import com.baegopa.onestep.dto.WeatherDTO;

public interface IWeatherService {

    String apiURL = "https://api.openweathermap.org/data/3.0/onecall";

    WeatherDTO getWeather(WeatherDTO pDTO) throws Exception;
}

