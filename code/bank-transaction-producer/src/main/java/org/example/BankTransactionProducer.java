package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class BankTransactionProducer {
    private static final Logger logger = LoggerFactory.getLogger(BankTransactionProducer.class);
    private static final String TOPIC = "bank-transactions";
    private static final int MESSAGES_PER_SECOND = 100;
    private static final ObjectMapper objectMapper = JsonConfiguration.buildObjectMapper();

    public static void main(String[] args) {
        KafkaProducer<String, String> producer = createKafkaProducer();
        Runtime.getRuntime().addShutdownHook(new Thread(producer::close));
        RandomBankTransactionFactory transactionFactory = new RandomBankTransactionFactory();
        while (true) {
            BankTransaction transaction = transactionFactory.build();
            send(producer, transaction);
            try {
                Thread.sleep(1000 / MESSAGES_PER_SECOND);
            } catch (InterruptedException e) {
                logger.error("Thread interrupted", e);
                break;
            }
        }
    }

    private static void send(KafkaProducer<String, String> producer, BankTransaction transaction) {
        try {
            String value = objectMapper.writeValueAsString(transaction);
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, null, value);
            producer.send(record, (recordMetadata, e) -> {
                if (e != null) {
                    logger.error("Error sending message", e);
                }
            });
        } catch (JsonProcessingException e) {
            logger.error("Error writing JSON", e);
        }
    }

    private static KafkaProducer<String, String> createKafkaProducer() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // configure safe producer
        properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
        properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

        // configure high throughput producer
        properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");
        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024));

        return new KafkaProducer<>(properties);
    }
}
