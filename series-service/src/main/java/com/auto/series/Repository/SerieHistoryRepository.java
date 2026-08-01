package com.auto.series.Repository;

import com.auto.series.Entity.SeriesHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SerieHistoryRepository extends JpaRepository<SeriesHistory, String> {
    Page<SeriesHistory> findBySeriesIdOrderByModifiedAtDesc(String seriesId, Pageable pageable);
}
