package com.baegopa.onestep.service.impl;

import com.baegopa.onestep.dto.WeatherDTO;
import com.baegopa.onestep.dto.WeatherDailyDTO;
import com.baegopa.onestep.service.IWeatherService;
import com.baegopa.onestep.util.CmmUtil;
import com.baegopa.onestep.util.DateUtil;
import com.baegopa.onestep.util.NetworkUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value; // lombok.Value가 아닌 이걸 써야 합니다!
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper; // 프로젝트 환경에 맞게 com.fasterxml.jackson.databind.ObjectMapper 일수도 있습니다.

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WeatherService implements IWeatherService {

    @Value("${weather.api.key}")
    private String apikey;

    @Cacheable(cacheNames = "weather",
            keyGenerator = "latLonKeyGen",
            sync = true)
    @Override
    public WeatherDTO getWeather(WeatherDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getWeather Start!");

        String lat = CmmUtil.nvl(pDTO.getLat());
        String lon = CmmUtil.nvl(pDTO.getLon());

        String apiParam = "?lat=" + lat + "&lon=" + lon + "&appid=" + apikey + "&units=metric";

        String json = NetworkUtil.get(IWeatherService.apiURL + apiParam);
        log.info("json : " + json);

        Map<String, Object> rMap = new ObjectMapper().readValue(json, LinkedHashMap.class);

        // temp와 weather를 모두 꺼내기 위해 Map<String, Object>로 받습니다.
        Map<String, Object> current = (Map<String, Object>) rMap.get("current");

        // Object를 String으로 바꾼 후 Double로 파싱하여 안전하게 기온을 가져옵니다.
        double currentTemp = Double.parseDouble(current.get("temp").toString());
        log.info("현재기온 : " + currentTemp);

        // 1. 현재 날씨 상태와 아이콘 추출
        List<Map<String, Object>> currentWeatherList = (List<Map<String, Object>>) current.get("weather");
        String weatherMain = "";
        String weatherIcon = "";
        if (currentWeatherList != null && !currentWeatherList.isEmpty()) {
            Map<String, Object> weatherData = currentWeatherList.get(0);
            weatherMain = (String) weatherData.get("main");
            weatherIcon = (String) weatherData.get("icon");
        }

        // 2. 데일리 리스트(주간 날씨) 추출
        List<Map<String, Object>> dailyList = (List<Map<String, Object>>) rMap.get("daily");
        List<WeatherDailyDTO> pList = new LinkedList<>();

        for (Map<String, Object> dailyMap : dailyList) {
            String day = DateUtil.getLongDateTime(dailyMap.get("dt"), "yyyy-MM-dd");
            String sunrise = DateUtil.getLongDateTime(dailyMap.get("sunrise"));
            String sunset = DateUtil.getLongDateTime(dailyMap.get("sunset"));
            String moonrise = DateUtil.getLongDateTime(dailyMap.get("moonrise"));
            String moonset = DateUtil.getLongDateTime(dailyMap.get("moonset"));

            Map<String, Double> dailyTemp = (Map<String, Double>) dailyMap.get("temp");

            String dayTemp = String.valueOf(dailyTemp.get("day"));
            String dayTempMax = String.valueOf(dailyTemp.get("max"));
            String dayTempMin = String.valueOf(dailyTemp.get("min"));

            WeatherDailyDTO wdDTO = new WeatherDailyDTO();
            wdDTO.setDay(day);
            wdDTO.setSunrise(sunrise);
            wdDTO.setSunset(sunset);
            wdDTO.setMoonrise(moonrise);
            wdDTO.setMoonset(moonset);
            wdDTO.setDayTemp(dayTemp);
            wdDTO.setDayTempMax(dayTempMax);
            wdDTO.setDayTempMin(dayTempMin);

            pList.add(wdDTO);
        }

        // 3. 최종적으로 DTO 하나를 생성해서 모든 데이터를 담아줍니다.
        WeatherDTO rDTO = new WeatherDTO();
        rDTO.setLat(lat);
        rDTO.setLon(lon);
        rDTO.setCurrentTemp(currentTemp);
        rDTO.setCurrentWeather(weatherMain); // 추출한 날씨 텍스트
        rDTO.setCurrentIcon(weatherIcon);    // 추출한 아이콘 코드
        rDTO.setDailyList(pList);

        log.info(this.getClass().getName() + ".getWeather End!");

        return rDTO;
    }
}