package com.sanja.temperatureconversion.controller;

import com.sanja.temperatureconversion.dto.TemperatureResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/temperature")
public class TemperatureController {

    @GetMapping("/convert")
    public TemperatureResponse convertCelsiusToFahrenheit(@RequestParam double celsius) {
        double fahrenheit = (celsius * 9.0 / 5.0) + 32.0;
        return new TemperatureResponse(celsius, fahrenheit);
    }
}
