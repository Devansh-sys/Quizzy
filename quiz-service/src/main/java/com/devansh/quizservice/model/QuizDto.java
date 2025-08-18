package com.devansh.quizservice.model;

import lombok.Data;

@Data
public class QuizDto {
    String categoryName;
    Integer numQuestions;
    String title;
}

