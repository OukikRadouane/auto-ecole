package com.auto.series.Repository;

import com.auto.series.Entity.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, String>{

    List<ExamQuestion> findByExamIdOrderByOrderIndexAsc(String examId);

    Optional<ExamQuestion> findByIdAndExamId(String examQuestionId, String examId);

    long countByExamId(String examId);
}
