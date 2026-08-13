package com.auto.registration.service;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    private static final long PRESIGNED_URL_EXPIRY = 3600;

    public String uploadFile(MultipartFile file, String folder) {
        try {
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
        } catch (Exception e) {
            log.error("Erreur lors de l'upload du fichier : {}", e.getMessage());
            throw new RuntimeException("Échec de l'upload du fichier", e);
        }
    }

    public String getFileUrl(String fileKey) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(fileKey)
                            .method(Method.GET)
                            .expiry((int) PRESIGNED_URL_EXPIRY)
                            .build()
            );
        } catch (Exception e) {
            log.error("Erreur lors de la génération de l'URL : {}", e.getMessage());
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
            log.error("Erreur lors de la suppression : {}", e.getMessage());
        }
    }
}