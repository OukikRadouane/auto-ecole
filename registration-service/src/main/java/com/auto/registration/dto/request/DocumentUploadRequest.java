package com.auto.registration.dto.request;

import com.auto.registration.enums.DocumentType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUploadRequest {

    @NotNull(message = "L'ID de la demande est obligatoire")
    private UUID registrationId;

    @NotNull(message = "Le type de document est obligatoire")
    private DocumentType documentType;

    @NotNull(message = "Le fichier est obligatoire")
    private MultipartFile file;
}