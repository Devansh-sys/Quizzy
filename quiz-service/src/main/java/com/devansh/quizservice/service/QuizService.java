package com.devansh.quizservice.service;

import com.devansh.quizservice.dao.QuizDao;
import com.devansh.quizservice.exception.ResourceNotFoundException;
import com.devansh.quizservice.feign.QuizInterface;
import com.devansh.quizservice.feign.UserInterface;
import com.devansh.quizservice.dto.QuizResponseDto;
import com.devansh.quizservice.model.QuestionWrapper;
import com.devansh.quizservice.model.Quiz;
import com.devansh.quizservice.model.QuizDto;
import com.devansh.quizservice.model.Response;
import com.devansh.quizservice.model.AIGenerateQuizRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private QuizDao quizDao;

    @Autowired
    private QuizInterface quizInterface;

    @Autowired
    private UserInterface userInterface;

    @Autowired
    private AIService aiService;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private RateLimiterService rateLimiterService;
    

    public ResponseEntity<String> createQuiz(QuizDto quizDto, String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = getUserIdFromAuthHeader(authHeader);
            
            // Validate user exists
            validateUserExists(userId, authHeader);
            
            // Get questions for the quiz
            List<Integer> questions = quizInterface.getQuestionsForQuiz(
                quizDto.getCategoryName(), 
                quizDto.getNumQuestions()
            ).getBody();
            
            // Create and save the quiz
            Quiz quiz = new Quiz(quizDto.getTitle(), userId, questions);
            quizDao.save(quiz);

            return new ResponseEntity<>("Quiz created successfully", HttpStatus.CREATED);
            
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating quiz", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getQuizQuestions(Integer id, String authHeader) {
        try {
            // Find the quiz
            Quiz quiz = quizDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));
                
            // Extract user ID from JWT token
            Long userId = getUserIdFromAuthHeader(authHeader);
            
            // Check if the user is the creator of the quiz
            if (!quiz.getUserId().equals(userId)) {
                return new ResponseEntity<>("Unauthorized access to quiz", HttpStatus.FORBIDDEN);
            }
            
            List<Integer> questionIds = quiz.getQuestionIds();
            if (questionIds == null || questionIds.isEmpty()) {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            }
            ResponseEntity<List<QuestionWrapper>> questions = quizInterface.getQuestionsFromId(questionIds);
            return questions;
            
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching quiz questions", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses, String authHeader) {
        try {
            // Find the quiz
            Quiz quiz = quizDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));
                
            // Extract user ID from JWT token
            Long userId = getUserIdFromAuthHeader(authHeader);
            
            // Check if the user is the creator of the quiz
            if (!quiz.getUserId().equals(userId)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            List<Integer> questionIds = quiz.getQuestionIds();
            int right = 0;
            int i = 0;
            for(Response response : responses){
                if(response.getResponse().equals(questionIds.get(i)))
                    right++;
                i++;
            }
            return new ResponseEntity<>(right, HttpStatus.OK);
            
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error calculating quiz result", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
