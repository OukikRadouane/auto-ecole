package com.auto.tutorial.service;

import com.auto.tutorial.dto.request.ContentCreateRequest;
import com.auto.tutorial.dto.request.ContentUpdateRequest;
import com.auto.tutorial.dto.response.ContentResponse;

import java.util.List;

public interface ContentService {

    ContentResponse createContent(String tutorialId, ContentCreateRequest request);
    ContentResponse updateContent(String contentId, ContentUpdateRequest request);
    void deleteContent(String contentId);
    ContentResponse getContent(String contentId);
    List<ContentResponse> getContentsByTutorial(String tutorialId);
    void reorderContents(String tutorialId, List<String> contentIds);
}