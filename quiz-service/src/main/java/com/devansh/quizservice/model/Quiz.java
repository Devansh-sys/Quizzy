package com.devansh.quizservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "quizzes")  // Explicit table name
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private Long userId; // ID of the user who created the quiz

    @ElementCollection
    private List<Integer> questionIds;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Quiz(String title, Long userId, List<Integer> questionIds) {
        this.title = title;
        this.userId = userId;
        this.questionIds = questionIds;
        this.createdAt = LocalDateTime.now();
    }
}

