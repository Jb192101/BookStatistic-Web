package org.jedi_bachelor.bookstatistic.analyzeservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.jedi_bachelor.bookstatistic.analyzeservice.redis.entity.TextFile;

@Configuration
public class RedisConfiguration {
    @Value("${spring.data.redis.host:localhost}")
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private Integer port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(this.host);
        factory.setPort(this.port);
        return factory;
    }

    @Bean
    public RedisTemplate<String, TextFile> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, TextFile> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}