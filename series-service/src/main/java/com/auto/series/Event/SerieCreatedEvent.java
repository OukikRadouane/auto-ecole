package com.auto.series.Event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SerieCreatedEvent {
    private String serieId;
    private String title;
    private String theme;
    private String createdBy;
    private LocalDateTime occurredAt;
}
