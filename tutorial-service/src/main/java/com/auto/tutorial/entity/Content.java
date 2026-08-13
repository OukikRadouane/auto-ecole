package com.auto.tutorial.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "contents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutorial_id", nullable = false)
    private Tutorial tutorial;

    // ─── TYPE ───
    @Column(name = "content_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    // ─── STOCKAGE ───
    @Column(name = "storage_url", nullable = false, length = 500)
    private String storageUrl;

    @Column(name = "file_key", length = 500)
    private String fileKey;

    @Column(name = "file_type", length = 100)
    private String fileType;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "is_required")
    @Builder.Default
    private Boolean isRequired = true;

    // ─── SPÉCIFIQUE VIDÉO ───
    @Column(name = "duration")
    private Integer duration;

    @Column(name = "thumbnail", length = 500)
    private String thumbnail;

    @Column(name = "transcoding_status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TranscodingStatus transcodingStatus = TranscodingStatus.PENDING;

    // ─── SPÉCIFIQUE PDF ───
    @Column(name = "page_count")
    private Integer pageCount;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ContentType {
        VIDEO, PDF, IMAGE, DOCUMENT
    }

    public enum TranscodingStatus {
        PENDING, PROCESSING, COMPLETED, FAILED
    }

    // ─── MÉTHODES UTILES ───

    public boolean isVideo() {
        return contentType == ContentType.VIDEO;
    }

    public boolean isPdf() {
        return contentType == ContentType.PDF;
    }

    public boolean hasDuration() {
        return isVideo() && duration != null && duration > 0;
    }

    public String getFormattedDuration() {
        if (!hasDuration()) {
            return null;
        }
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public String getDurationDisplay() {
        if (isPdf()) {
            return "PDF";
        }
        if (!hasDuration()) {
            return "N/A";
        }
        return getFormattedDuration();
    }

    public String getPageCountDisplay() {
        if (isPdf() && pageCount != null && pageCount > 0) {
            return pageCount + " pages";
        }
        return null;
    }
}