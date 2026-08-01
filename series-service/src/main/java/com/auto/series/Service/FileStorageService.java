package com.auto.series.Service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String upload(MultipartFile file, String bucket, String folder);
    void delete(String objectUrl, String bucket);
    String getPresignedUrl(String objectKey, String bucket, int expirySeconds);
}
