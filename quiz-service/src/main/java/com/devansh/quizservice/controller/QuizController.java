package com.devansh.quizservice.controller;

import com.devansh.quizservice.model.QuestionWrapper;
import com.devansh.quizservice.model.QuizDto;
import com.devansh.quizservice.model.Response;
import com.devansh.quizservice.model.AIGenerateQuizRequest;
import com.devansh.quizservice.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("quiz")
public class QuizController {

    @Autowired
    QuizService quizService;

    @PostMapping("create")
    public ResponseEntity<String> createQuiz(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody QuizDto quizDto
    ) {
        return quizService.createQuiz(quizDto, authHeader);
    }

    @GetMapping("get/{id}")
    public ResponseEntity<?> getQuizQuestions(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id
    ) {
        return quizService.getQuizQuestions(id, authHeader);
    }

    @PostMapping("submit/{id}")
    public ResponseEntity<?> submitQuiz(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id, 
            @RequestBody List<Response> responses
    ) {
        return quizService.calculateResult(id, responses, authHeader);
    }

    @PostMapping("generate-with-ai")
    public ResponseEntity<String> createQuizWithAI(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AIGenerateQuizRequest request
    ) {
        return quizService.createQuizWithAI(request, authHeader);
    }

    /**
     * Get all quizzes for the current user (without question details)
     * @param authHeader Authorization header with JWT token
     * @return List of quizzes (without questions)
     */
    @GetMapping("user")
    public ResponseEntity<?> getUserQuizzes(
            @RequestHeader("Authorization") String authHeader
    ) {
        return quizService.getUserQuizzes(authHeader, false);
    }

    /**
     * Get all quizzes for the current user with question details
     * @param authHeader Authorization header with JWT token
     * @return List of quizzes with question details
     */
    @GetMapping("user/details")
    public ResponseEntity<?> getUserQuizzesWithDetails(
            @RequestHeader("Authorization") String authHeader
    ) {
        return quizService.getUserQuizzes(authHeader, true);
    }

    /**
     * Get a specific quiz with questions for the current user
     * @param quizId ID of the quiz to retrieve
     * @param authHeader Authorization header with JWT token
     * @return Quiz details with questions
     */
    @GetMapping("user/{quizId}")
    public ResponseEntity<?> getUserQuiz(
            @PathVariable Integer quizId,
            @RequestHeader("Authorization") String authHeader
    ) {
        return quizService.getUserQuiz(quizId, authHeader);
    }
}
