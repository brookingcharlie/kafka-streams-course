package org.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Properties;

class BankBalanceStreamsTest {
    private TopologyTestDriver testDriver;
    private TestInputTopic<String, BankTransaction> inputTopic;
    private TestOutputTopic<String, BankBalance> outputTopic;

    @BeforeEach
    public void setUp() {
        Topology topology = BankBalanceStreams.buildTopology();
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "bank-balance");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
        testDriver = new TopologyTestDriver(topology, config);

        JsonSerializer<BankTransaction> bankTransactionSerializer = new JsonSerializer<>();
        JsonDeserializer bankBalanceDeserializer = new JsonDeserializer();
        bankBalanceDeserializer.configure(new HashMap<String, Object>() {{
            put("valueType", BankBalance.class);
        }}, false);
        inputTopic = testDriver.createInputTopic("bank-transactions", new StringSerializer(), bankTransactionSerializer);
        outputTopic = testDriver.createOutputTopic("bank-balances", new StringDeserializer(), bankBalanceDeserializer);
    }

    @AfterEach
    void tearDown() {
        testDriver.close();
    }

    @Test
    public void test() {
        inputTopic.pipeInput("Fred", new BankTransaction("Fred", 5, LocalDateTime.parse("2001-01-01T01:00:00")));
        inputTopic.pipeInput("Judy", new BankTransaction("Judy", 4, LocalDateTime.parse("2001-01-01T01:30:00")));
        inputTopic.pipeInput("Fred", new BankTransaction("Fred", 3, LocalDateTime.parse("2001-01-01T02:00:00")));
        Assertions.assertEquals(new BankBalance("Fred", 5, LocalDateTime.parse("2001-01-01T01:00:00")), outputTopic.readKeyValue().value);
        Assertions.assertEquals(new BankBalance("Judy", 4, LocalDateTime.parse("2001-01-01T01:30:00")), outputTopic.readKeyValue().value);
        Assertions.assertEquals(new BankBalance("Fred", 8, LocalDateTime.parse("2001-01-01T02:00:00")), outputTopic.readKeyValue().value);
    }
}