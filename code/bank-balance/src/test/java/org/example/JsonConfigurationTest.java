package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class JsonConfigurationTest {
    @Test
    public void serialisesDateTimesCorrectly() throws JsonProcessingException {
        ObjectMapper objectMapper = JsonConfiguration.buildObjectMapper();
        BankTransaction transaction = new BankTransaction("John", 123, LocalDateTime.of(2017, 7, 19, 5, 24, 52));
        String output = objectMapper.writeValueAsString(transaction);
        assertEquals("{\"name\":\"John\",\"amount\":123,\"time\":\"2017-07-19T05:24:52\"}", output);
    }
}