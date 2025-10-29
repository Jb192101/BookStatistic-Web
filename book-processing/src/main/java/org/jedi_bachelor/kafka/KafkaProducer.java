package org.jedi_bachelor.kafka;

import org.jedi_bachelor.kafka.dto.KafkaDtoMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(String topic, KafkaDtoMessage message) {
        kafkaTemplate.send(topic, message);
    }
}
