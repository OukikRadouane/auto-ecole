package com.auto.tutorial.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class PdfProcessingService {

    /**
     * Extrait le nombre de pages d'un fichier PDF
     */
    public Integer extractPageCount(File pdfFile) {
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            int pageCount = document.getNumberOfPages();
            log.info("Nombre de pages extrait : {}", pageCount);
            return pageCount;
        } catch (IOException e) {
            log.error("Erreur lors de l'extraction du nombre de pages : {}", e.getMessage());
            return null;
        }
    }
}