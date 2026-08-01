package com.auto.series.Repository;

import com.auto.series.Entity.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExamResultRepository extends JpaRepository<ExamResult, String>{

    Optional<ExamResult> findByExamId(String examId);

    List<ExamResult> findByUserIdOrderByCompletedAtDesc(String userId);

    // GET /api/series/{id}/statistics — moyenne des scores sur les examens
    // basés sur les séries données (nécessite un JOIN via ExamQuestion.questionId -> Question.seriesId,
    // fait au niveau service plutôt qu'ici pour rester simple)

    @Query("SELECT AVG(er.score) FROM ExamResult er WHERE er.userId = :userId")
    Double findAverageScoreByUserId(@Param("userId") String userId);

    long countByUserIdAndPassedTrue(String userId);

    long countByUserId(String userId);
}
