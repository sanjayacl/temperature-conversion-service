package com.sanja.temperatureconversion.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TemperatureService {

    private static final Logger logger = LoggerFactory.getLogger(TemperatureService.class);

    public double celsiusToFahrenheit(double celsius) {
        logger.debug("Converting {} Celsius to Fahrenheit", celsius);
        double fahrenheit = (celsius * 9.0 / 5.0) + 32.0;
        logger.debug("Conversion result: {} Celsius = {} Fahrenheit", celsius, fahrenheit);
        return fahrenheit;
    }
}
