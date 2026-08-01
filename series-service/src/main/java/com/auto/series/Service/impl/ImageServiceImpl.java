package com.auto.series.Service.impl;

import com.auto.series.Exception.InvalidFileException;
import com.auto.series.Service.FileStorageService;
import com.auto.series.Service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024; //5 Mo

    private final FileStorageService fileStorageService;

    @Value("${minio.buckets.images}")
    private String imagesBucket;

    @Override
    public String uploadQuestionImage(MultipartFile file, String questionId) {
        validate(file);
        return fileStorageService.upload(file, imagesBucket, "questions/" + questionId);
    }

    @Override
    public void deleteQuestionImage(String imageUrl) {
        fileStorageService.delete(imageUrl, imagesBucket);
    }

    private void validate(MultipartFile file){
        if (file == null || file.isEmpty()){
            throw new InvalidFileException("Aucun fichier fourni");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())){
            throw new InvalidFileException("Format d'image non supporté (jpeg, png, webp uniquement)");
        }
        if (file.getSize() > MAX_SIZE_BYTES){
            throw new InvalidFileException("L'image dépasse la taille maximale autorisée (5Mo)");
        }
    }
}
