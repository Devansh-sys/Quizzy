package com.devansh.quizservice.dao;

import com.devansh.quizservice.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizDao extends JpaRepository<Quiz, Integer> {
    
    /**
     * Find all quizzes created by a specific user
     * @param userId The ID of the user
     * @return List of quizzes created by the user, ordered by creation date (newest first)
     */
    List<Quiz> findByUserIdOrderByCreatedAtDesc(Long userId);
}

