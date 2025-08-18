package com.devansh.quizservice.dao;


import com.devansh.quizservice.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizDao extends JpaRepository<Quiz,Integer> {
}

