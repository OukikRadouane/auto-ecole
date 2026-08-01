package com.auto.series.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "series_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriesHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "series_id", nullable = false)
    private String seriesId;

    @Column(name = "modified_by", nullable = false)
    private String modifiedBy;// userId de l'admin

    @Column(name = "field_changed", nullable = false)
    private String fieldChanged;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @CreationTimestamp
    @Column(name = "modified_at", updatable = false)
    private LocalDateTime modifiedAt;
}
