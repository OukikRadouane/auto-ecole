package com.auto.series.Service.impl;

import com.auto.series.Exception.InvalidFileException;
import com.auto.series.Service.AudioService;
import com.auto.series.Service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AudioServiceImpl implements AudioService {

    private static final Set<String> ALLOWED_TYPES = Set.of("audio/mpeg", "audio/mp4", "audio/wav");
    private static final long MAX_SIZE_BYTES = 10 * 1024 * 1024;

    private final FileStorageService fileStorageService;

    @Value("${minio.buckets.audios}")
    private String audiosBucket;

    @Override
    public String uploadQuestionAudio(MultipartFile file, String questionId) {
        validate(file);
        return fileStorageService.upload(file, audiosBucket, "questions/" + questionId);
    }

    @Override
    public void deleteQuestionAudio(String audioUrl) {
        fileStorageService.delete(audioUrl, audiosBucket);
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Aucun fichier fourni");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new InvalidFileException("Format audio non supporté (mp3, m4a, wav uniquement)");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new InvalidFileException("Le fichier audio dépasse la taille maximale autorisée (10 Mo)");
        }
    }
}
