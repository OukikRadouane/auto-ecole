package com.auto.series.Service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String uploadQuestionImage(MultipartFile file, String questionId);
    void deleteQuestionImage(String imageUrl);
}
