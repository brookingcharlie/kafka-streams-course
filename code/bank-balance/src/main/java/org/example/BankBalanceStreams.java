package org.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

import java.util.HashMap;
import java.util.Properties;

public class BankBalanceStreams {
    public static final String APPLICATION_ID = "bank-balance-3";
    public static final String INPUT_TOPIC = "bank-transactions";
    public static final String OUTPUT_TOPIC = "bank-balances";

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, BankTransaction> input = builder.stream(
                INPUT_TOPIC,
                Consumed.with(Serdes.String(), buildJsonSerde(BankTransaction.class))
        );
        KStream<String, BankBalance> output = input
                .groupByKey()
                .aggregate(
                        () -> new BankBalance(null, 0, null),
                        (key, transaction, prevBalance) -> new BankBalance(
                                transaction.getName(),
                                prevBalance.getBalance() + transaction.getAmount(),
                                transaction.getTime()
                        ),
                        Materialized.with(Serdes.String(), buildJsonSerde(BankBalance.class))
                )
                .toStream();
        output.to(OUTPUT_TOPIC, Produced.with(Serdes.String(), buildJsonSerde(BankBalance.class)));

        KafkaStreams streams = new KafkaStreams(builder.build(), config);
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        streams.start();
    }

    private static <T> Serde<T> buildJsonSerde(final Class<T> valueType) {
        Serializer<T> serializer = new JsonSerializer<>();
        Deserializer<T> deserializer = new JsonDeserializer<>();
        deserializer.configure(new HashMap<String, Object>() {{
            put("valueType", valueType);
        }}, false);
        return Serdes.serdeFrom(serializer, deserializer);
    }
}
