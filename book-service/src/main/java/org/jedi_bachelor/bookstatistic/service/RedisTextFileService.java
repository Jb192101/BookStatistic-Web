package org.jedi_bachelor.bookstatistic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTextFileService {
    private final RedisTemplate<String, Object> redisTemplate;

}
