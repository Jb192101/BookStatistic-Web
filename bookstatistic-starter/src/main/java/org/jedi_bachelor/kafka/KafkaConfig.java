package org.jedi_bachelor.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Bean
    public NewTopic accountTopic() {
        return TopicBuilder
                .name("account-topic")
                .partitions(3)
                .build();
    }

    @Bean
    public NewTopic bookTopic() {
        return TopicBuilder
                .name("book-topic")
                .partitions(3)
                .build();
    }

    @Bean
    public NewTopic readingTopic() {
        return TopicBuilder
                .name("reading-topic")
                .partitions(3)
                .build();
    }
}
