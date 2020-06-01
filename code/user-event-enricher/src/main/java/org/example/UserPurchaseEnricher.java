package org.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import java.util.HashMap;
import java.util.Properties;

public class UserPurchaseEnricher {
    public static final String APPLICATION_ID = "user-event-enricher-2";
    public static final String PROFILES_TOPIC = "user-profiles";
    public static final String PURCHASES_TOPIC = "user-purchases";
    public static final String OUTPUT_TOPIC = "enriched-user-purchases";

    public static void main(String[] args) {
        KafkaStreams streams = new KafkaStreams(buildTopology(), buildConfig());
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        streams.start();
    }

    public static Properties buildConfig() {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
        return config;
    }

    public static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();
        GlobalKTable<String, UserProfile> profiles = builder.globalTable(
                PROFILES_TOPIC,
                Consumed.with(Serdes.String(), buildJsonSerde(UserProfile.class)),
                Materialized.as("user-profiles")
        );
        KStream<String, UserPurchase> input = builder.stream(
                PURCHASES_TOPIC,
                Consumed.with(Serdes.String(), buildJsonSerde(UserPurchase.class))
        );
        KStream<String, EnrichedUserPurchase> output = input
                .leftJoin(
                        profiles,
                        (key, value) -> key,
                        (purchase, profile) -> new EnrichedUserPurchase(purchase, profile),
                        Named.as("user-purchase-enricher")
                );
        output.to(OUTPUT_TOPIC, Produced.with(Serdes.String(), buildJsonSerde(EnrichedUserPurchase.class)));
        return builder.build();
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
