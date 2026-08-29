package com.foodreels.backend.service;

import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PersonalizedFeedCacheService {

    private final StringRedisTemplate redisTemplate;

    public PersonalizedFeedCacheService(
            StringRedisTemplate redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    public void evictUserPersonalizedFeed(
            String email) {

        String pattern =
                "personalizedFeed::"
                        + email
                        + ":page:*";

        Set<String> keys =
                redisTemplate.keys(pattern);

        if (keys != null
                && !keys.isEmpty()) {

            redisTemplate.delete(keys);
        }

        System.out.println(
                "PERSONALIZED CACHE EVICTED FOR: "
                        + email
        );
    }
}