package com.example.producer;

import com.example.model.UserEvent;
import com.example.serialization.UserEventSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaEventProducer {

    private final KafkaProducer<String, UserEvent> producer;
    private final String topic;

    public KafkaEventProducer(
            String bootstrapServers,
            String topic
    ) {

        Properties props = new Properties();

        props.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        props.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName()
        );

        props.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                UserEventSerializer.class.getName()
        );

        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.LINGER_MS_CONFIG, 20);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 64_000);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");

        this.producer = new KafkaProducer<>(props);
        this.topic = topic;
    }

    public void send(UserEvent event) {

        ProducerRecord<String, UserEvent> record =
                new ProducerRecord<>(
                        topic,
                        String.valueOf(event.userId()),
                        event
                );

        producer.send(record);
    }

    public void close() {
        producer.close();
    }
}
