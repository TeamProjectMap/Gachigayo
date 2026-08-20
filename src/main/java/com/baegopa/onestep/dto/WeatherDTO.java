package com.baegopa.onestep.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class WeatherDTO implements Serializable {
    private String lat; // 위도
    private String lon; // 경도
    private double currentTemp; // 현재 기온

    // UI 표현을 위해 아래 두 필드를 추가합니다.
    private String currentWeather; // 현재 날씨 상태 (예: Rain, Clear, Clouds)
    private String currentIcon; // 날씨 아이콘 코드 (예: 10d, 01n)

    private List<WeatherDailyDTO> dailyList;
}