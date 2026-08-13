package com.auto.tutorial.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tutorials")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tutorial {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "estimated_duration")
    private Integer estimatedDuration;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.DRAFT;

    @Column(name = "access_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AccessType accessType = AccessType.FREE;

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @OneToMany(mappedBy = "tutorial", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<Content> contents = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    public enum Difficulty {
        BEGINNER, INTERMEDIATE, ADVANCED
    }

    public enum Status {
        DRAFT, PUBLISHED, ARCHIVED
    }

    public enum AccessType {
        FREE, PREMIUM
    }

    public void addContent(Content content) {
        if (this.contents == null) {
            this.contents = new ArrayList<>();
        }
        this.contents.add(content);
        content.setTutorial(this);
    }

    public int getTotalContents() {
        return contents != null ? contents.size() : 0;
    }

    public int getTotalDuration() {
        if (contents == null) return 0;
        return contents.stream()
                .filter(c -> c.isVideo() && c.getDuration() != null)
                .mapToInt(Content::getDuration)
                .sum();
    }

    public void incrementViewCount() {
        if (viewCount == null) viewCount = 0;
        viewCount++;
    }

    public boolean hasVideo() {
        return contents != null && contents.stream().anyMatch(Content::isVideo);
    }

    public boolean hasPdf() {
        return contents != null && contents.stream().anyMatch(Content::isPdf);
    }
}