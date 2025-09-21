package com.devansh.quizservice.aspect;

import com.devansh.quizservice.exception.RateLimitExceededException;
import com.devansh.quizservice.service.RateLimiterService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RateLimitAspect {

    private final RateLimiterService rateLimiterService;

    @Autowired
    public RateLimitAspect(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Around("@annotation(com.devansh.quizservice.annotation.RateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get the user ID from the method arguments
        // Assuming the first argument is the userId or contains it
        Object[] args = joinPoint.getArgs();
        Long userId = null;
        
        // Try to find Long userId in method arguments
        for (Object arg : args) {
            if (arg instanceof Long) {
                userId = (Long) arg;
                break;
            } else if (arg instanceof String && ((String) arg).startsWith("Bearer ")) {
                // If it's a JWT token, extract userId (simplified example)
                // In a real app, you'd decode the JWT to get the userId
                String token = ((String) arg).substring(7);
                try {
                    // This is a simplified example - in a real app, use a proper JWT library
                    String[] parts = token.split("\\.");
                    if (parts.length > 1) {
                        // This is just a placeholder - in reality, you'd decode the JWT properly
                        userId = 1L; // Default user ID for demo
                    }
                } catch (Exception e) {
                    throw new SecurityException("Invalid token");
                }
                break;
            }
        }

        if (userId == null) {
            throw new SecurityException("User ID not found in request");
        }

        // Check rate limit
        if (!rateLimiterService.isAllowed(userId)) {
            long retryAfter = rateLimiterService.getTimeUntilReset(userId);
            throw new RateLimitExceededException(
                String.format("Rate limit exceeded. Try again in %d seconds.", retryAfter),
                retryAfter
            );
        }

        // Proceed with the method call
        return joinPoint.proceed();
    }
}
