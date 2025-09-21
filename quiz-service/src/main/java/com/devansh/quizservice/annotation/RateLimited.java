package com.devansh.quizservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods that should be rate limited.
 * Methods annotated with this will be intercepted by RateLimitAspect.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {
    /**
     * Custom rate limit (requests per duration)
     */
    int value() default 0;
    
    /**
     * Duration in seconds for the rate limit window
     */
    int duration() default 60;
}
