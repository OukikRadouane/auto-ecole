package com.auto.tutorial.service;

import com.auto.tutorial.dto.request.TutorialCreateRequest;
import com.auto.tutorial.dto.request.TutorialUpdateRequest;
import com.auto.tutorial.dto.response.TutorialDetailResponse;
import com.auto.tutorial.dto.response.TutorialResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TutorialService {

    TutorialResponse createTutorial(TutorialCreateRequest request);
    Page<TutorialResponse> getPublishedTutorials(Pageable pageable, String search, String category, String difficulty);
    TutorialDetailResponse getTutorialDetail(String id);
    List<TutorialResponse> getTutorialsByCategory(String categoryId);
    List<TutorialResponse> getPublishedTutorialsList();
    TutorialResponse updateTutorial(String id, TutorialUpdateRequest request);
    TutorialResponse publishTutorial(String id);
    TutorialResponse archiveTutorial(String id);
    void deleteTutorial(String id);
    Page<TutorialResponse> getAllTutorials(Pageable pageable, String search, String status);
}