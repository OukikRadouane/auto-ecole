package com.auto.tutorial.repository;

import com.auto.tutorial.entity.Content;
import com.auto.tutorial.entity.Content.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, String> {

    // ─── ORDER ───
    List<Content> findByTutorialIdOrderByDisplayOrderAsc(String tutorialId);

    // ─── BY TYPE ───
    List<Content> findByTutorialIdAndContentTypeOrderByDisplayOrderAsc(String tutorialId, ContentType contentType);

    // ─── DELETION ───
    @Modifying
    @Transactional
    @Query("DELETE FROM Content c WHERE c.tutorial.id = :tutorialId")
    void deleteByTutorialId(@Param("tutorialId") String tutorialId);

    // ─── COUNTS ───
    long countByTutorialId(String tutorialId);
    long countByTutorialIdAndContentType(String tutorialId, ContentType contentType);

    // ─── CHECK ───
    @Query("SELECT COUNT(c) > 0 FROM Content c WHERE c.tutorial.id = :tutorialId AND c.contentType = 'VIDEO'")
    boolean hasVideo(@Param("tutorialId") String tutorialId);

    @Query("SELECT COUNT(c) > 0 FROM Content c WHERE c.tutorial.id = :tutorialId AND c.contentType = 'PDF'")
    boolean hasPdf(@Param("tutorialId") String tutorialId);
}