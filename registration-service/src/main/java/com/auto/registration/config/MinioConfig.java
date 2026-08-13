package com.auto.registration.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket}")
    private String bucket;

    @Bean
    public MinioClient minioClient() {
        try {
            MinioClient client = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            boolean exists = client.bucketExists(
                    io.minio.BucketExistsArgs.builder().bucket(bucket).build()
            );

            if (!exists) {
                client.makeBucket(
                        io.minio.MakeBucketArgs.builder().bucket(bucket).build()
                );
                log.info("Bucket '{}' créé avec succès", bucket);
            }

            return client;
        } catch (Exception e) {
            log.error("Erreur MinIO : {}", e.getMessage());
            throw new RuntimeException("Configuration MinIO échouée", e);
        }
    }
}