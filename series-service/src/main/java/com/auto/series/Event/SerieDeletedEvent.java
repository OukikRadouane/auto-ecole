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
public class SerieDeletedEvent {
    private String serieId;
    private String deletedBy;
    private LocalDateTime occurredAt;
}
