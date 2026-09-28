package com.sanja.temperatureconversion.controller;

import com.sanja.temperatureconversion.dto.TemperatureResponse;
import com.sanja.temperatureconversion.service.TemperatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/temperature")
public class TemperatureController {

    private static final Logger logger = LoggerFactory.getLogger(TemperatureController.class);

    private final TemperatureService temperatureService;

    public TemperatureController(TemperatureService temperatureService) {
        this.temperatureService = temperatureService;
    }

    @GetMapping("/convert")
    public TemperatureResponse convertCelsiusToFahrenheit(@RequestParam double celsius) {
        logger.info("Received request to convert {} Celsius to Fahrenheit", celsius);
        double fahrenheit = temperatureService.celsiusToFahrenheit(celsius);
        logger.info("Returning conversion response: {} Celsius = {} Fahrenheit", celsius, fahrenheit);
        return new TemperatureResponse(celsius, fahrenheit);
    }
}
