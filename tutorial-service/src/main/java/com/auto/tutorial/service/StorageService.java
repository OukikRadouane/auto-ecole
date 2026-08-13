package com.auto.tutorial.service;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    private static final int PRESIGNED_URL_EXPIRY = 3600;

    // ─── UPLOAD DIRECTEMENT DEPUIS MULTIPART FILE ───
    public String uploadFile(MultipartFile file, String folder) throws Exception {
        String fileKey = folder + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(fileKey)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        log.info("Fichier uploadé vers MinIO : {}", fileKey);
        return fileKey;
    }

    // ─── UPLOAD DEPUIS UN FICHIER LOCAL (NOUVEAU) ───
    public String uploadFileFromLocal(File file, String folder, String contentType) throws Exception {
        String fileKey = folder + "/" + UUID.randomUUID() + "-" + file.getName();

        try (FileInputStream inputStream = new FileInputStream(file)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileKey)
                            .stream(inputStream, file.length(), -1)
                            .contentType(contentType)
                            .build()
            );
        }

        log.info("Fichier uploadé vers MinIO : {}", fileKey);
        return fileKey;
    }

    public String getFileUrl(String fileKey) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(fileKey)
                            .method(Method.GET)
                            .expiry(PRESIGNED_URL_EXPIRY)
                            .build()
            );
        } catch (Exception e) {
            log.error("Erreur URL : {}", e.getMessage());
            return null;
        }
    }

    public void deleteFile(String fileKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileKey)
                            .build()
            );
            log.info("Fichier supprimé : {}", fileKey);
        } catch (Exception e) {
            log.error("Erreur suppression : {}", e.getMessage());
        }
    }

    public boolean fileExists(String fileKey) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileKey)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}