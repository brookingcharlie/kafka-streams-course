package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.stream.Collectors;

public class UserEventProducer {
    private static final Logger logger = LoggerFactory.getLogger(UserEventProducer.class);
    private static final String PROFILES_TOPIC = "user-profiles";
    private static final String PURCHASES_TOPIC = "user-purchases";
    private static final int NUM_EMAIL_CHANGES = 10;
    private static final int NUM_PURCHASES_PER_EMAIL_CHANGE = 5;
    private static final int MAX_EVENT_INTERVAL = 1000;

    private static KafkaProducer<String, UserProfile> profileProducer;
    private static KafkaProducer<String, UserPurchase> purchaseProducer;
    private Random random = new Random();

    public UserEventProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        config.put(ProducerConfig.LINGER_MS_CONFIG, "1");
        profileProducer = new KafkaProducer<>(config, new StringSerializer(), new JsonSerializer<>());
        purchaseProducer = new KafkaProducer<>(config, new StringSerializer(), new JsonSerializer<>());
        Runtime.getRuntime().addShutdownHook(new Thread(profileProducer::close));
        Runtime.getRuntime().addShutdownHook(new Thread(purchaseProducer::close));
    }

    public void run() {
        List<String> names = Arrays.asList("fred", "wilma", "barney", "betty");
        List<Thread> threads = names.stream()
                .map(name -> new Thread(() -> goShopping(name)))
                .collect(Collectors.toList());
        threads.forEach(Thread::start);
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            logger.warn("main thread interrupted", e);
            return;
        }
    }

    private void goShopping(String name) {
        try {
            for (int i = 0; i < NUM_EMAIL_CHANGES; i++) {
                Thread.sleep(random.nextInt(MAX_EVENT_INTERVAL));
                String email = name + "+" + i + "@bedrock.org";
                UserProfile profile = new UserProfile(name, email);
                profileProducer.send(new ProducerRecord<>(PROFILES_TOPIC, name, profile), (metadata, e) -> {
                    if (e != null) logger.error("Error sending profile", e);
                });

                for (int j = 0; j < NUM_PURCHASES_PER_EMAIL_CHANGE; j++) {
                    Thread.sleep(random.nextInt(MAX_EVENT_INTERVAL));
                    String product = "product#" + j;
                    UserPurchase purchase = new UserPurchase(name, product);
                    purchaseProducer.send(new ProducerRecord<>(PURCHASES_TOPIC, name, purchase), (metadata, e) -> {
                        if (e != null) logger.error("Error sending purchase", e);
                    });
                }
            }
        } catch (InterruptedException e) {
            logger.warn(name + " thread interrupted", e);
            return;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new UserEventProducer().run();
    }
}