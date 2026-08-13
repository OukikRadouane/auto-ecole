package com.auto.series.Repository;

import com.auto.series.Entity.Question;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, String>{

    // GET /api/series/{seriesId}/questions
    List<Question> findBySeriesIdOrderByOrderIndexAsc(String seriesId);

    // GET /api/series/{seriesId}/questions/{questionId}
    Optional<Question> findByIdAndSeriesId(String questionId, String seriesId);

    long countBySeriesId(String seriesId);

    @Query("SELECT q FROM Question q WHERE q.serieId IN :seriesIds")
    List<Question> findAllBySeriesIdIn(@Param("seriesIds") List<String> seriesIds);

    // pour décaler les index lors d'un réordonnancement
    @Modifying
    @Transactional
    @Query("UPDATE Question q SET q.orderIndex = :orderIndex WHERE q.id = :questionId")
    void updateOrderIndex(@Param("questionId") String questionId, @Param("orderIndex") Integer orderIndex);

    void deleteBySerieId(String seriesId);

    Object countBySerieId(String serieId);

    List<Question> findBySerieIdOrderByOrderIndexAsc(String serieId);

    Optional<Question> findByIdAndSerieId(String questionId, String serieId);

    List<Question> findAllBySerieIdIn(@NotEmpty List<String> seriesIds);
}
