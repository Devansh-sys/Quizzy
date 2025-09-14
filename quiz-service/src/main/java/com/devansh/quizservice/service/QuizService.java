package com.devansh.quizservice.service;

import com.devansh.quizservice.dao.QuizDao;
import com.devansh.quizservice.feign.QuizInterface;
import com.devansh.quizservice.model.QuestionWrapper;
import com.devansh.quizservice.model.Quiz;
import com.devansh.quizservice.model.QuizDto;
import com.devansh.quizservice.model.Response;
import com.devansh.quizservice.model.AIGenerateQuizRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    QuizDao quizDao;

    @Autowired
    QuizInterface quizInterface;

    @Autowired
    private AIService aiService;

    @Autowired
    private ObjectMapper objectMapper;

    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {

        List<Integer> questions = quizInterface.getQuestionsForQuiz(category, numQ).getBody();
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionIds(questions);
        quizDao.save(quiz);

        return new ResponseEntity<>("Success", HttpStatus.CREATED);

    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
          Quiz quiz = quizDao.findById(id).get();
          List<Integer> questionIds = quiz.getQuestionIds();
          ResponseEntity<List<QuestionWrapper>> questions = quizInterface.getQuestionsFromId(questionIds);
          return questions;

    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
        ResponseEntity<Integer> score = quizInterface.getScore(responses);
        return score;
    }

    public ResponseEntity<String> createQuizWithAI(AIGenerateQuizRequest request) {
        try {
            // Generate questions using AI
            List<Question> aiQuestions = aiService.generateQuestions(
                request.getCategory(),
                request.getDifficultyLevel(),
                request.getRoleType(),
                request.getYearsOfExperience(),
                request.getNumQuestions()
            );

            // Save questions to database and get their IDs
            List<Integer> questionIds = new ArrayList<>();
            for (Question question : aiQuestions) {
                // Save each question and collect their IDs
                ResponseEntity<Integer> response = quizInterface.addQuestion(question);
                if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                    questionIds.add(response.getBody());
                }
            }

            // Create quiz with the generated question IDs
            Quiz quiz = new Quiz();
            quiz.setTitle(request.getQuizTitle());
            quiz.setQuestionIds(questionIds);
            quizDao.save(quiz);

            return new ResponseEntity<>("AI-generated quiz created successfully with ID: " + quiz.getId(), HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error creating AI-generated quiz: " + e.getMessage(), 
                                     HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
