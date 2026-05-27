package com.sanja.temperatureconversion.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TemperatureController.class)
class TemperatureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void convertFreezingPoint() throws Exception {
        mockMvc.perform(get("/api/temperature/convert").param("celsius", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.celsius").value(0.0))
                .andExpect(jsonPath("$.fahrenheit").value(32.0));
    }

    @Test
    void convertBoilingPoint() throws Exception {
        mockMvc.perform(get("/api/temperature/convert").param("celsius", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.celsius").value(100.0))
                .andExpect(jsonPath("$.fahrenheit").value(212.0));
    }

    @Test
    void convertNormalBodyTemperature() throws Exception {
        // 37°C = 98.6°F
        mockMvc.perform(get("/api/temperature/convert").param("celsius", "37"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.celsius").value(37.0))
                .andExpect(jsonPath("$.fahrenheit").value(98.6));
    }

    @Test
    void convertFeverThreshold() throws Exception {
        // 38°C = 100.4°F — the clinical fever boundary
        mockMvc.perform(get("/api/temperature/convert").param("celsius", "38"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.celsius").value(38.0))
                .andExpect(jsonPath("$.fahrenheit").value(100.4));
    }

    @Test
    void convertNegativeTemperature() throws Exception {
        // -40°C = -40°F — the crossover point
        mockMvc.perform(get("/api/temperature/convert").param("celsius", "-40"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.celsius").value(-40.0))
                .andExpect(jsonPath("$.fahrenheit").value(-40.0));
    }
}
