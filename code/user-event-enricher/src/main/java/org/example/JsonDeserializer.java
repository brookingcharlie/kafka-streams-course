package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class JsonDeserializer<T> implements Deserializer<T> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Class<T> valueType;

    public JsonDeserializer() {
    }

    public JsonDeserializer(Class<T> valueType) {
        this.valueType = valueType;
    }

    @Override
    public void configure(Map<String, ?> props, boolean isKey) {
        valueType = (Class<T>) props.get("valueType");
    }

    @Override
    public T deserialize(String topic, byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            return objectMapper.readValue(bytes, valueType);
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }
}
