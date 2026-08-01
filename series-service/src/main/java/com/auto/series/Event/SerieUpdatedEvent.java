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
public class SerieUpdatedEvent {
    private String serieId;
    private String modifiedBy;
    private LocalDateTime occurredAt;
}
