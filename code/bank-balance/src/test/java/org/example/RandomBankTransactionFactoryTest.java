package org.example;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class RandomBankTransactionFactoryTest {
    @Test
    public void buildsWithConstrainedValues() {
        Clock clock = Clock.fixed(Instant.parse("2007-12-03T10:15:30.00Z"), ZoneId.of("UTC"));
        RandomBankTransactionFactory factory =
            new RandomBankTransactionFactory(Arrays.asList("Fred", "Sally", "Wilma"), -3, 3, clock);
        for (int i = 0; i < 100; i++) {
            BankTransaction transaction = factory.build();
            assertTrue(transaction.getName().matches("Fred|Sally|Wilma"));
            assertTrue(transaction.getAmount() >= -3);
            assertTrue(transaction.getAmount() <= 3);
            assertEquals(LocalDateTime.parse("2007-12-03T10:15:30"), transaction.getTime());
        }
    }
}