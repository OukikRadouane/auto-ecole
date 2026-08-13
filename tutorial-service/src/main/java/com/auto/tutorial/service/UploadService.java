package com.auto.tutorial.service;

import com.auto.tutorial.dto.response.ContentResponse;
import com.auto.tutorial.entity.Content;
import com.auto.tutorial.entity.Tutorial;
import com.auto.tutorial.exception.ResourceNotFoundException;
import com.auto.tutorial.mapper.ContentMapper;
import com.auto.tutorial.repository.ContentRepository;
import com.auto.tutorial.repository.TutorialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UploadService {

    private final TutorialRepository tutorialRepository;
    private final ContentRepository contentRepository;
    private final StorageService storageService;
    private final VideoProcessingService videoProcessingService;
    private final PdfProcessingService pdfProcessingService;
    private final ContentMapper contentMapper;

    @Value("${ffmpeg.temp-dir:/tmp/tutorial-ffmpeg}")
    private String tempDir;

    public ContentResponse uploadVideo(String tutorialId, MultipartFile file, String title, String description) {
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new ResourceNotFoundException("Tutoriel non trouvé : " + tutorialId));

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new IllegalArgumentException("Type de fichier invalide. Seules les vidéos sont autorisées.");
        }

        String tempFilePath = null;
        String thumbnailPath = null;
        File tempFile = null;

        try {
            // ─── 1. CRÉER LE RÉPERTOIRE TEMPORAIRE ───
            Path tempDirPath = Paths.get(tempDir);
            if (!Files.exists(tempDirPath)) {
                Files.createDirectories(tempDirPath);
                log.info("Répertoire temporaire créé : {}", tempDir);
            }

            // ─── 2. CRÉER LE SOUS-RÉPERTOIRE POUR LES MINIATURES ───
            String thumbnailDir = tempDir + "/thumbnails";
            Path thumbnailDirPath = Paths.get(thumbnailDir);
            if (!Files.exists(thumbnailDirPath)) {
                Files.createDirectories(thumbnailDirPath);
                log.info("Répertoire des miniatures créé : {}", thumbnailDir);
            }

            // ─── 3. NETTOYER LE NOM DU FICHIER ───
            String originalFileName = file.getOriginalFilename();
            String safeFileName = originalFileName != null
                    ? originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_")
                    : "video.mp4";

            // ─── 4. SAUVEGARDER LE FICHIER ───
            tempFilePath = tempDir + "/" + UUID.randomUUID() + "-" + safeFileName;
            tempFile = new File(tempFilePath);
            file.transferTo(tempFile);
            log.info("Fichier temporaire sauvegardé : {}", tempFilePath);

            if (!tempFile.exists()) {
                throw new IOException("Le fichier temporaire n'a pas été créé : " + tempFilePath);
            }

            // ─── 5. EXTRAIRE LA DURÉE ───
            CompletableFuture<Integer> durationFuture = videoProcessingService.extractDuration(tempFilePath);
            int duration = durationFuture.join();
            log.info("Durée extraite : {} secondes", duration);

            // ─── 6. GÉNÉRER LA MINIATURE ───
            CompletableFuture<String> thumbnailFuture = videoProcessingService.generateThumbnail(tempFilePath, thumbnailDir);
            thumbnailPath = thumbnailFuture.join();
            log.info("Miniature générée : {}", thumbnailPath);

            // ─── 7. UPLOAD LA VIDÉO VERS MINIO (DEPUIS LE FICHIER LOCAL) ───
            String fileKey = storageService.uploadFileFromLocal(tempFile, "videos", contentType);
            String storageUrl = storageService.getFileUrl(fileKey);

            // ─── 8. UPLOAD LA MINIATURE VERS MINIO ───
            String thumbnailUrl = null;
            if (thumbnailPath != null && new File(thumbnailPath).exists()) {
                try {
                    File thumbnailFile = new File(thumbnailPath);
                    String thumbnailKey = storageService.uploadFileFromLocal(
                            thumbnailFile,
                            "thumbnails",
                            "image/jpeg"
                    );
                    thumbnailUrl = storageService.getFileUrl(thumbnailKey);
                    log.info("Miniature uploadée : {}", thumbnailUrl);
                } catch (Exception e) {
                    log.warn("Impossible d'uploader la miniature : {}", e.getMessage());
                }
            }

            // ─── 9. CRÉER L'ENTITÉ CONTENT ───
            Content content = Content.builder()
                    .title(title != null ? title : file.getOriginalFilename())
                    .description(description)
                    .contentType(Content.ContentType.VIDEO)
                    .storageUrl(storageUrl)
                    .fileKey(fileKey)
                    .fileType(contentType)
                    .duration(duration)
                    .thumbnail(thumbnailUrl)
                    .transcodingStatus(Content.TranscodingStatus.COMPLETED)
                    .isRequired(true)
                    .tutorial(tutorial)
                    .build();

            long count = contentRepository.countByTutorialId(tutorialId);
            content.setDisplayOrder((int) count);

            Content saved = contentRepository.saveAndFlush(content);
            log.info("Vidéo uploadée avec succès : {}", saved.getId());

            return contentMapper.toResponse(saved);

        } catch (Exception e) {
            log.error("Erreur lors de l'upload de la vidéo : {}", e.getMessage(), e);
            throw new RuntimeException("Échec de l'upload de la vidéo", e);
        } finally {
            // ─── 10. NETTOYER LES FICHIERS TEMPORAIRES ───
            videoProcessingService.cleanupTempFiles(tempFilePath, thumbnailPath);
        }
    }

    public ContentResponse uploadPdf(String tutorialId, MultipartFile file, String title, String description) {
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new ResourceNotFoundException("Tutoriel non trouvé : " + tutorialId));

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new IllegalArgumentException("Type de fichier invalide. Seuls les PDF sont autorisés.");
        }

        String tempFilePath = null;
        File tempFile = null;

        try {
            // ─── 1. CRÉER LE RÉPERTOIRE TEMPORAIRE ───
            Path tempDirPath = Paths.get(tempDir);
            if (!Files.exists(tempDirPath)) {
                Files.createDirectories(tempDirPath);
                log.info("Répertoire temporaire créé : {}", tempDir);
            }

            // ─── 2. NETTOYER LE NOM DU FICHIER ───
            String originalFileName = file.getOriginalFilename();
            String safeFileName = originalFileName != null
                    ? originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_")
                    : "document.pdf";

            // ─── 3. SAUVEGARDER LE FICHIER EN LOCAL (nécessaire pour PDFBox) ───
            tempFilePath = tempDir + "/" + UUID.randomUUID() + "-" + safeFileName;
            tempFile = new File(tempFilePath);
            file.transferTo(tempFile);
            log.info("Fichier PDF temporaire sauvegardé : {}", tempFilePath);

            if (!tempFile.exists()) {
                throw new IOException("Le fichier temporaire n'a pas été créé : " + tempFilePath);
            }

            // ─── 4. EXTRAIRE LE NOMBRE DE PAGES AUTOMATIQUEMENT ───
            Integer pageCount = pdfProcessingService.extractPageCount(tempFile);
            log.info("Nombre de pages détecté : {}", pageCount);

            // ─── 5. UPLOAD LE PDF VERS MINIO (DEPUIS LE FICHIER LOCAL) ───
            String fileKey = storageService.uploadFileFromLocal(tempFile, "pdfs", contentType);
            String storageUrl = storageService.getFileUrl(fileKey);

            // ─── 6. CRÉER L'ENTITÉ CONTENT ───
            Content content = Content.builder()
                    .title(title != null ? title : file.getOriginalFilename())
                    .description(description)
                    .contentType(Content.ContentType.PDF)
                    .storageUrl(storageUrl)
                    .fileKey(fileKey)
                    .fileType(contentType)
                    .duration(null)
                    .thumbnail(null)
                    .transcodingStatus(Content.TranscodingStatus.COMPLETED)
                    .pageCount(pageCount)
                    .isRequired(true)
                    .tutorial(tutorial)
                    .build();

            long count = contentRepository.countByTutorialId(tutorialId);
            content.setDisplayOrder((int) count);

            Content saved = contentRepository.saveAndFlush(content);
            log.info("PDF uploadé avec succès : {}", saved.getId());

            return contentMapper.toResponse(saved);

        } catch (Exception e) {
            log.error("Erreur lors de l'upload du PDF : {}", e.getMessage(), e);
            throw new RuntimeException("Échec de l'upload du PDF", e);
        } finally {
            // ─── 7. NETTOYER LE FICHIER TEMPORAIRE ───
            videoProcessingService.cleanupTempFiles(tempFilePath);
        }
    }
}