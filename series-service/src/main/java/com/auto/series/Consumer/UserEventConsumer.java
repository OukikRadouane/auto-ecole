package com.auto.series.Consumer;

import com.auto.series.Entity.Exam;
import com.auto.series.Event.consumed.UserDeletedEvent;
import com.auto.series.Repository.ExamRepository;
import com.auto.series.Repository.ExamResultRepository;
import com.auto.series.Repository.UserAnswerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final ExamRepository examRepository;
    private final ExamResultRepository examResultRepository;
    private final UserAnswerRepository userAnswerRepository;

    public void onUserDeleted(UserDeletedEvent event){
        log.info("Suppression des données d'examen pour userId={}", event.getUserId());

        // ordre important : UserAnswer référence ExamQuestion/Exam, donc à nettoyer en premier

        List<Exam> exams = examRepository.findByUserIdOrderByStartedAtDesc(event.getUserId());
        for (Exam exam : exams){
            userAnswerRepository.deleteAll(userAnswerRepository.findByExamId(exam.getId()));
            examResultRepository.findByExamId(exam.getId()).ifPresent(examResultRepository::delete);
        }
        examRepository.deleteAll(exams);

        log.info("Nettoyage terminé pour userId={} ({} examens supprimés)", event.getUserId(), exams.size());
    }
}
