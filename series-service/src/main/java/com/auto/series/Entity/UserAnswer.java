package com.auto.series.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_answers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "exam_id", nullable = false)
    private String examId;

    @Column(name = "exam_question_id", nullable = false)
    private String examQuestionId;

    @Column(name = "question_id", nullable = false)
    private String questionId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ElementCollection
    @CollectionTable(name = "user_answer_selections", joinColumns = @JoinColumn(name = "user_answer_id"))
    @Column(name = "selected_index", nullable = false)
    @Builder.Default
    private List<Integer> selectedAnswerIndices = new ArrayList<>(); // vide si pas de réponse donnée

    @Column(nullable = false)
    private boolean correct;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;
}
