package com.devansh.quizservice.service;

import com.devansh.quizservice.annotation.RateLimited;
import com.devansh.quizservice.exception.RateLimitExceededException;
import com.devansh.quizservice.model.Question;
import com.google.cloud.aiplatform.v1beta1.*;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.project.id}")
    private String projectId;

    @Value("${gemini.location:us-central1}")
    private String location;
    
    private final RateLimiterService rateLimiterService;
    
    @Autowired
    public AIService(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @RateLimited
    public List<Question> generateQuestions(String category, String difficulty, String roleType, int yearsOfExp, int count, String authHeader) throws IOException {
        // Extract user ID from auth header (in a real app, decode JWT properly)
        Long userId = extractUserIdFromAuthHeader(authHeader);
        
        // Check rate limit
        if (!rateLimiterService.isAllowed(userId)) {
            long retryAfter = rateLimiterService.getTimeUntilReset(userId);
            throw new RateLimitExceededException(
                String.format("Rate limit exceeded. Try again in %d seconds.", retryAfter),
                retryAfter
            );
        }
        String prompt = String.format("""
            Generate %d multiple-choice questions about %s for a %s with %d years of experience.
            Difficulty level: %s
            
            For each question, provide:
            1. The question text
            2. 4 options (A, B, C, D)
            3. The correct answer (A, B, C, or D)
            
            Format the response as a JSON array of objects with these fields:
            - questionTitle: The question text
            - option1, option2, option3, option4: The multiple choice options
            - rightAnswer: The correct option (1-4)
            - difficultyLevel: The difficulty level (%s)
            - category: The category (%s)
            
            Example response:
            [
                {
                    "questionTitle": "What is the output of this code?",
                    "option1": "10",
                    "option2": "20",
                    "option3": "30",
                    "option4": "40",
                    "rightAnswer": 2,
                    "difficultyLevel": "%s",
                    "category": "%s"
                }
            ]
            """,
            count, category, roleType, yearsOfExp, difficulty, difficulty, category, difficulty, category);

        try (PredictionServiceClient client = PredictionServiceClient.create()) {
            String endpoint = String.format("%s-aiplatform.googleapis.com:443", location);
            String model = "projects/%s/locations/%s/publishers/google/models/gemini-pro";
            model = String.format(model, projectId, location);

            String contents = String.format("""
                {
                    "contents": [{
                        "parts": [{"text": "%s"}]
                    }]
                }
                """, prompt.replace("\"", "\\\""));

            Value.Builder instanceValue = Value.newBuilder();
            JsonFormat.parser().merge(contents, instanceValue);
            List<Value> instances = List.of(instanceValue.build());

            Value parameters = Value.newBuilder()
                .setStructValue(com.google.protobuf.Struct.newBuilder()
                    .putFields("temperature", Value.newBuilder().setNumberValue(0.2).build())
                    .putFields("maxOutputTokens", Value.newBuilder().setNumberValue(2048).build())
                    .build())
                .build();

            PredictResponse response = client.predict(model, instances, parameters);
            String jsonResponse = response.getPredictions(0).getStructValue()
                .getFieldsOrThrow("candidates")
                .getListValue()
                .getValues(0)
                .getStructValue()
                .getFieldsOrThrow("content")
                .getStructValue()
                .getFieldsOrThrow("parts")
                .getListValue()
                .getValues(0)
                .getStructValue()
                .getFieldsOrThrow("text")
                .getStringValue();

            // Parse the JSON response and convert to Question objects
            return parseQuestionsFromJson(jsonResponse);
        }
    }

    private List<Question> parseQuestionsFromJson(String json) {
        // This is a simplified parser - you might want to use Jackson or Gson in a real implementation
        List<Question> questions = new ArrayList<>();
        // Parse the JSON and create Question objects
        // This is a placeholder - implement actual JSON parsing based on your Question class structure
        // For now, returning empty list as we'll implement this properly in the next step
        return questions;
    }
    
    private Long extractUserIdFromAuthHeader(String authHeader) {
        try {
            // This is a simplified example - in a real app, extract user ID from JWT
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                // In a real app, decode the JWT to get the user ID
                // For now, return a default user ID (1) for testing
                return 1L;
            }
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing authorization token");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authorization token");
        }
    }
    }
}
