package com.devansh.quizservice.model;

import lombok.Data;

@Data
public class AIGenerateQuizRequest {
    private String category;
    private String difficultyLevel; // EASY, MEDIUM, HARD
    private String roleType; // e.g., "JAVA_DEVELOPER", "PYTHON_DEVELOPER"
    private int yearsOfExperience;
    private int numQuestions;
    private String quizTitle;
}
