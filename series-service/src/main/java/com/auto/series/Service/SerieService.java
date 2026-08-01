package com.auto.series.Service;

import com.auto.series.Dto.Request.SerieFilterRequest;
import com.auto.series.Dto.Request.SerieRequest;
import com.auto.series.Dto.Response.PageResponse;
import com.auto.series.Dto.Response.SerieDetailResponse;
import com.auto.series.Dto.Response.SerieResponse;

public interface SerieService {
    SerieResponse create(SerieRequest request, String createBy);
    PageResponse<SerieResponse> findAll(SerieFilterRequest filter);
    PageResponse<SerieResponse> findPremium(SerieFilterRequest filter);
    PageResponse<SerieResponse> findFree(SerieFilterRequest filter);
    PageResponse<SerieResponse> findByTheme(String theme, SerieFilterRequest filter);
    SerieDetailResponse findById(String id);
    SerieResponse update(String id, SerieRequest request, String modifiedBy);
    void delete(String id, String modifiedBy);
    void togglePremium(String id, String modifiedBy);
    SerieResponse duplicate(String id, String createdBy);
    void archive(String id, String modifiedBy);
}
