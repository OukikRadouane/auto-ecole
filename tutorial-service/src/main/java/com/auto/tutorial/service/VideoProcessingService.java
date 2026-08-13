package com.auto.tutorial.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class VideoProcessingService {

    @Value("${ffmpeg.path:/usr/local/bin/ffmpeg}")
    private String ffmpegPath;

    @Value("${ffmpeg.ffprobe-path:/usr/local/bin/ffprobe}")
    private String ffprobePath;

    @Value("${ffmpeg.temp-dir:/tmp/tutorial-ffmpeg}")
    private String tempDir;

    @Value("${app.storage.video-thumbnail-time:5}")
    private int thumbnailTime;

    private static final String FFMPEG_CONTAINER = "tutorial-ffmpeg";

    /**
     * Convertit un chemin Windows en chemin compatible avec le conteneur Docker
     */
    private String convertToContainerPath(String localPath) {
        String path = localPath.replace("\\", "/");

        String normalized = path.replaceAll(".*/tutorial-ffmpeg", "/tmp/tutorial-ffmpeg");

        if (!normalized.startsWith("/tmp/")) {
            normalized = normalized.replaceAll(".*tmp[/\\\\]tutorial-ffmpeg", "/tmp/tutorial-ffmpeg");
        }

        return normalized;
    }

    /**
     * Extrait la durée d'une vidéo (Async - avec Docker)
     */
    public CompletableFuture<Integer> extractDuration(String filePath) {
        return CompletableFuture.supplyAsync(() -> {
            Process process = null;
            try {
                Path path = Paths.get(filePath);
                if (!Files.exists(path)) {
                    log.error("Fichier non trouvé : {}", filePath);
                    return 0;
                }

                String containerPath = convertToContainerPath(filePath);
                log.debug("Chemin local : {} → Chemin conteneur : {}", filePath, containerPath);

                ProcessBuilder pb = new ProcessBuilder(
                        "docker", "exec", FFMPEG_CONTAINER,
                        ffprobePath,
                        "-v", "error",
                        "-show_entries", "format=duration",
                        "-of", "default=noprint_wrappers=1:nokey=1",
                        containerPath
                );

                process = pb.start();

                String line;
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    line = reader.readLine();
                }

                boolean finished = process.waitFor(10, TimeUnit.SECONDS);
                if (!finished) {
                    process.destroyForcibly();
                    log.warn("Extraction de durée : timeout après 10s, processus tué");
                    return 0;
                }

                if (line != null && !line.isEmpty()) {
                    double duration = Double.parseDouble(line.trim());
                    int seconds = (int) Math.round(duration);
                    log.info("Durée extraite : {} secondes", seconds);
                    return seconds;
                }

                log.warn("ffprobe n'a retourné aucune durée exploitable");
                return 0;

            } catch (Exception e) {
                log.error("Erreur lors de l'extraction de la durée : {}", e.getMessage());
                if (process != null && process.isAlive()) {
                    process.destroyForcibly();
                }
                return 0;
            }
        });
    }

    /**
     * Génère une miniature (Async - avec Docker)
     */
    public CompletableFuture<String> generateThumbnail(String inputPath, String outputDir) {
        return CompletableFuture.supplyAsync(() -> {
            Process process = null;
            try {
                File dir = new File(outputDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String baseName = FilenameUtils.getBaseName(new File(inputPath).getName());
                String outputPath = outputDir + "/" + baseName + ".jpg";

                if (new File(outputPath).exists()) {
                    log.info("Miniature existe déjà : {}", outputPath);
                    return outputPath;
                }

                String dockerInputPath = convertToContainerPath(inputPath);
                String dockerOutputPath = convertToContainerPath(outputPath);

                log.debug("Input conteneur : {}, Output conteneur : {}", dockerInputPath, dockerOutputPath);

                ProcessBuilder pb = new ProcessBuilder(
                        "docker", "exec", FFMPEG_CONTAINER,
                        ffmpegPath,
                        "-ss", String.valueOf(thumbnailTime),
                        "-i", dockerInputPath,
                        "-vframes", "1",
                        "-q:v", "2",
                        "-vf", "scale=320:180",
                        "-y",
                        dockerOutputPath
                );

                pb.redirectErrorStream(true);
                process = pb.start();

                // Consommer le flux de sortie pour éviter un blocage si le buffer se remplit
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    while (reader.readLine() != null) {
                        // on ignore le contenu, juste pour vider le buffer
                    }
                }

                boolean finished = process.waitFor(30, TimeUnit.SECONDS);
                if (!finished) {
                    process.destroyForcibly();
                    log.warn("Génération miniature : timeout après 30s, processus tué");
                    return null;
                }

                int exitCode = process.exitValue();

                if (exitCode == 0 && new File(outputPath).exists()) {
                    log.info("Miniature générée : {}", outputPath);
                    return outputPath;
                } else {
                    log.error("Échec de la génération de la miniature, code : {}", exitCode);
                    return null;
                }

            } catch (Exception e) {
                log.error("Erreur lors de la génération de la miniature : {}", e.getMessage());
                if (process != null && process.isAlive()) {
                    process.destroyForcibly();
                }
                return null;
            }
        });
    }

    /**
     * Nettoie les fichiers temporaires
     */
    public void cleanupTempFiles(String... filePaths) {
        for (String filePath : filePaths) {
            if (filePath != null) {
                try {
                    Path path = Paths.get(filePath);
                    if (Files.exists(path)) {
                        Files.delete(path);
                        log.debug("Fichier temporaire supprimé : {}", filePath);
                    }
                } catch (Exception e) {
                    log.warn("Impossible de supprimer le fichier temporaire : {}", filePath);
                }
            }
        }
    }
}