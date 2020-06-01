package org.example;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserPurchaseEnricherTest {
    private TopologyTestDriver testDriver;
    private TestInputTopic<String, UserPurchase> purchasesTopic;
    private TestInputTopic<String, UserProfile> profilesTopic;
    private TestOutputTopic<String, EnrichedUserPurchase> outputTopic;

    @BeforeEach
    public void setUp() {
        testDriver = new TopologyTestDriver(
                UserPurchaseEnricher.buildTopology(),
                UserPurchaseEnricher.buildConfig()
        );
        purchasesTopic = testDriver.createInputTopic(
                UserPurchaseEnricher.PURCHASES_TOPIC,
                new StringSerializer(),
                new JsonSerializer<>()
        );
        profilesTopic = testDriver.createInputTopic(
                UserPurchaseEnricher.PROFILES_TOPIC,
                new StringSerializer(),
                new JsonSerializer<>()
        );
        outputTopic = testDriver.createOutputTopic(
                UserPurchaseEnricher.OUTPUT_TOPIC,
                new StringDeserializer(),
                new JsonDeserializer(EnrichedUserPurchase.class)
        );
    }

    @AfterEach
    void tearDown() {
        testDriver.close();
    }

    @Test
    public void enrichesWhenProfileKnown() {
        UserProfile profile = new UserProfile("Fred", "fred@bedrock.org");
        UserPurchase purchase1 = new UserPurchase("Fred", "product#1");
        UserPurchase purchase2 = new UserPurchase("Fred", "product#2");

        profilesTopic.pipeInput(profile.getName(), profile);
        purchasesTopic.pipeInput(purchase1.getUser(), purchase1);
        purchasesTopic.pipeInput(purchase2.getUser(), purchase2);

        Assertions.assertEquals(new EnrichedUserPurchase(purchase1, profile), outputTopic.readKeyValue().value);
        Assertions.assertEquals(new EnrichedUserPurchase(purchase2, profile), outputTopic.readKeyValue().value);
    }

    @Test
    public void passesPurchaseThroughWhenProfileUnknown() {
        UserProfile profile = new UserProfile("Fred", "fred@bedrock.org");
        UserPurchase purchase1 = new UserPurchase("Fred", "product#1");
        UserPurchase purchase2 = new UserPurchase("Fred", "product#2");

        purchasesTopic.pipeInput(purchase1.getUser(), purchase1);
        profilesTopic.pipeInput(profile.getName(), profile);
        purchasesTopic.pipeInput(purchase2.getUser(), purchase2);

        Assertions.assertEquals(new EnrichedUserPurchase(purchase1, null), outputTopic.readKeyValue().value);
        Assertions.assertEquals(new EnrichedUserPurchase(purchase2, profile), outputTopic.readKeyValue().value);
    }
}