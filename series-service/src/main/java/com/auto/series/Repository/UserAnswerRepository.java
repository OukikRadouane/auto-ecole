package com.auto.series.Repository;

import com.auto.series.Entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, String> {

    List<UserAnswer> findByExamId(String examId);

    Optional<UserAnswer> findByExamQuestionIdAndUserId(String examQuestionId, String userId);

    // pour "examen personnalisé basé sur les points faibles" :
    // questions les plus ratées par un élève donné, groupées par questionId
    @Query("""
            SELECT ua.questionId FROM UserAnswer ua
            WHERE ua.userId = :userId AND ua.correct = false
            GROUP BY ua.questionId
            ORDER BY COUNT(ua.id) DESC
            """)
    List<String> findMostFailedQuestionIdsByUserId(@Param("userId") String userId);

    long countByUserIdAndCorrectFalse(String userId);

    long countByUserIdAndCorrectTrue(String userId);
}
