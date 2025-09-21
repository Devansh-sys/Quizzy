package com.devansh.quizservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterService {

    private final RedisTemplate<String, String> redisTemplate;
    private final long maxRequests;
    private final long durationInSeconds;
    private static final String RATE_LIMIT_KEY_PREFIX = "rate_limit:";

    @Autowired
    public RateLimiterService(
            RedisTemplate<String, String> redisTemplate,
            @Value("${app.rate-limit.requests:5}") long maxRequests,
            @Value("${app.rate-limit.duration:60}") long durationInSeconds) {
        this.redisTemplate = redisTemplate;
        this.maxRequests = maxRequests;
        this.durationInSeconds = durationInSeconds;
    }

    /**
     * Checks if the request is allowed based on the user's rate limit
     * @param userId The ID of the user making the request
     * @return true if the request is allowed, false if rate limit is exceeded
     */
    public boolean isAllowed(Long userId) {
        String key = RATE_LIMIT_KEY_PREFIX + userId;
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        
        // Get current count
        String currentCountStr = ops.get(key);
        int currentCount = 0;
        
        if (currentCountStr != null) {
            currentCount = Integer.parseInt(currentCountStr);
            
            // If limit is reached, deny the request
            if (currentCount >= maxRequests) {
                return false;
            }
        }
        
        // Increment the count and set expiration if this is the first request
        if (currentCount == 0) {
            ops.set(key, "1", durationInSeconds, TimeUnit.SECONDS);
        } else {
            // Increment the existing counter
            redisTemplate.opsForValue().increment(key);
        }
        
        return true;
    }
    
    /**
     * Gets the remaining requests for a user before hitting the rate limit
     * @param userId The ID of the user
     * @return Number of remaining requests, or -1 if no rate limit is set for the user
     */
    public long getRemainingRequests(Long userId) {
        String key = RATE_LIMIT_KEY_PREFIX + userId;
        String currentCountStr = redisTemplate.opsForValue().get(key);
        
        if (currentCountStr == null) {
            return maxRequests;
        }
        
        long currentCount = Long.parseLong(currentCountStr);
        return Math.max(0, maxRequests - currentCount);
    }
    
    /**
     * Gets the time remaining until the rate limit resets (in seconds)
     * @param userId The ID of the user
     * @return Time remaining in seconds, or 0 if no rate limit is active
     */
    public long getTimeUntilReset(Long userId) {
        String key = RATE_LIMIT_KEY_PREFIX + userId;
        Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire != null ? expire : 0;
    }
}
