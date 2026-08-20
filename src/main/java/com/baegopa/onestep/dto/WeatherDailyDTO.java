package com.baegopa.onestep.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class WeatherDailyDTO implements Serializable {

    private String day;

    private String sunrise;

    private String sunset;

    private String moonrise;

    private String moonset;

    private String dayTemp;

    private String dayTempMax;

    private String dayTempMin;
}

