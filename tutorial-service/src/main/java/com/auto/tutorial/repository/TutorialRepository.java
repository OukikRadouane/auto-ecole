package com.auto.tutorial.repository;

import com.auto.tutorial.entity.Tutorial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorialRepository extends JpaRepository<Tutorial, String> {

    Page<Tutorial> findByStatus(Tutorial.Status status, Pageable pageable);
    List<Tutorial> findByStatusOrderByCreatedAtDesc(Tutorial.Status status);
    Optional<Tutorial> findByIdAndStatus(String id, Tutorial.Status status);

    List<Tutorial> findByCategoryIdAndStatusOrderByDisplayOrderAsc(String categoryId, Tutorial.Status status);

    /*
    @Query("SELECT t FROM Tutorial t WHERE t.status = 'PUBLISHED' " +
            "AND (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:category IS NULL OR t.category.id = :category) " +
            "AND (:difficulty IS NULL OR t.difficulty = :difficulty)")
    Page<Tutorial> searchPublishedTutorials(
            @Param("search") String search,
            @Param("category") String category,
            @Param("difficulty") String difficulty,
            Pageable pageable);
*/
    @Query("SELECT t FROM Tutorial t WHERE t.status = 'PUBLISHED' " +
            "AND (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:category IS NULL OR t.category.id = :category) " +
            "AND (:difficulty IS NULL OR t.difficulty = :difficulty)")
    Page<Tutorial> searchPublishedTutorials(
            @Param("search") String search,
            @Param("category") String category,
            @Param("difficulty") Tutorial.Difficulty difficulty,  // ← Changer en enum
            Pageable pageable);
    @Modifying
    @Transactional
    @Query("UPDATE Tutorial t SET t.viewCount = t.viewCount + 1 WHERE t.id = :id")
    void incrementViewCount(@Param("id") String id);

    long countByStatus(Tutorial.Status status);
    boolean existsByIdAndStatus(String id, Tutorial.Status status);
}