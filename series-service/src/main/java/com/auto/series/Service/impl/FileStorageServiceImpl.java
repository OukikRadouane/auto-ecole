package com.auto.series.Service.impl;

import com.auto.series.Exception.FileStorageException;
import com.auto.series.Service.FileStorageService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {
    private final MinioClient minioClient;

    @Override
    public String upload(MultipartFile file, String bucket, String folder) {
        ensureBucketExists(bucket);

        String extension = extractExtension(file.getOriginalFilename());
        String objectKey = folder + "/" + UUID.randomUUID() + extension;
        try (InputStream inputStream = file.getInputStream()){
            minioClient.putObject(PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
        } catch (Exception e) {
            log.error("Échec de l'upload vers MinIO, bucket={}, objectKey={}", bucket, objectKey, e);
            throw new FileStorageException("Impossible d'uploader le fichier", e);
        }
        return objectKey;
    }

    @Override
    public void delete(String objectKey, String bucket) {

        if (objectKey == null || objectKey.isBlank()) return;
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .build());
        } catch (Exception e) {
            log.error("Échec de la suppression MinIO, bucket={}, objectKey={}", bucket, objectKey, e);
        }
    }

    @Override
    public String getPresignedUrl(String objectKey, String bucket, int expirySeconds) {
        if (objectKey == null || objectKey.isBlank()) return null;
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(objectKey)
                    .expiry(expirySeconds, TimeUnit.SECONDS)
                    .build());
        } catch (Exception e) {
            log.error("Échec de génération de l'URL présignée, bucket={}, objectKey={}", bucket, objectKey, e);
            throw new FileStorageException("Impossible de générer le lien d'accès au fichier", e);
        }
    }

    private void ensureBucketExists(String bucket) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception e) {
            throw new FileStorageException("Impossible de vérifier/créer le bucket " + bucket, e);
        }
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) return "";
        return originalFilename.substring(originalFilename.lastIndexOf('.'));
    }
}
