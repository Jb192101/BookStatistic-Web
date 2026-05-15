package org.jedi_bachelor.bookstatistic.analyzeservice.redis;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.analyzeservice.redis.entity.TextFile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisContentManager {
    private final RedisTemplate<String, TextFile> restTemplate;
}
