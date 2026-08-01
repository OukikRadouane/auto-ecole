package com.auto.series.Service;

import com.auto.series.Dto.Request.ExamSubmitRequest;
import com.auto.series.Dto.Request.RandomExamRequest;
import com.auto.series.Dto.Response.CorrectionResponse;
import com.auto.series.Dto.Response.ExamResponse;
import com.auto.series.Dto.Response.ExamResultResponse;

public interface ExamService {
    ExamResponse startExam(String serieId, String userId);
    ExamResponse startTraining(String serieId, String userId);
    ExamResponse startRandomExam(RandomExamRequest request, String userId);
    ExamResponse getExam(String examId, String userId);
    ExamResultResponse submitExam(String examId, ExamSubmitRequest request, String userId);
    CorrectionResponse getCorrection(String examId, String userId);
    ExamResponse resumeExam(String userId);
}
