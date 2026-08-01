package com.auto.series.Service;

import org.springframework.web.multipart.MultipartFile;

public interface AudioService {
    String uploadQuestionAudio(MultipartFile file, String questionId);
    void deleteQuestionAudio(String audioUrl);
}
