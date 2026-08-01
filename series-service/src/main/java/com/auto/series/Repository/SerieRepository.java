package com.auto.series.Repository;

import com.auto.series.Entity.Serie;
import com.auto.series.Enums.Difficulty;
import com.auto.series.Enums.SerieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, String> {
    //Get /api/series (pagination + filtres theme/premium/difficulty)
    @Query("""
            SELECT s FROM Serie s
            WHERE s.status = :status
            AND (:theme IS NULL OR s.theme = :theme)
            AND (:premium IS NULL OR s.premium = :premium)
            AND (:difficulty IS NULL OR s.difficulty = :difficulty)
""")
    Page<Serie> findWithFilters(
            @Param("status")SerieStatus status,
            @Param("theme") String theme,
            @Param("premium") Boolean premium,
            @Param("difficulty") Difficulty difficulty,
            Pageable pageable
            );

    //Get /api/series/premium
    Page<Serie> findByPremiumTrueAndStatus(SerieStatus status, Pageable pageable);

    //GET /api/series/free
    Page<Serie> findByPremiumFalseAndStatus(SerieStatus status, Pageable pageable);

    //GET /api/series/theme/{theme}
    Page<Serie> findByThemeAndStatus(String theme, SerieStatus status, Pageable pageable);

    // GET /api/series/{id} — exclut les séries supprimées
    Optional<Serie> findByIdAndStatusNot(String id, SerieStatus excludedStatus);

    List<Serie> findByStatus(SerieStatus status);

    boolean existsByTitleAndStatusNot(String title, SerieStatus excludedStatus);

    long countByStatus(SerieStatus status);
}
