package com.auto.series.Entity;

import com.auto.series.Enums.QuestionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exam_questions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "exam_id", nullable = false)
    private String examId;

    @Column(name = "serie_id", nullable = false)
    private String serieId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", insertable = false, updatable = false)
    private Exam exam;

    @Column(name = "question_id", nullable = false)
    private String questionId;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @ElementCollection
    @CollectionTable(name = "exam_question_options", joinColumns = @JoinColumn(name = "exam_question_id"))
    @Column(name = "option_text", nullable = false)
    @OrderColumn(name = "option_index")
    @Builder.Default
    private List<String> options = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "exam_question_correct_answers", joinColumns = @JoinColumn(name = "exam_question_id"))
    @Column(name = "answer_index", nullable = false)
    @Builder.Default
    private List<Integer> correctAnswerIndices = new ArrayList<>();


    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "tutorial_link")
    private String tutorialLink;
}
