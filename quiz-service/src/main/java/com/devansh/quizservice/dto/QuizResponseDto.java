package com.devansh.quizservice.dto;

import com.devansh.quizservice.model.QuestionWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponseDto {
    private Integer id;
    private String title;
    private LocalDateTime createdAt;
    private List<QuestionWrapper> questions;
    
    // For responses without questions
    public QuizResponseDto(Integer id, String title, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.questions = null;
    }
}
