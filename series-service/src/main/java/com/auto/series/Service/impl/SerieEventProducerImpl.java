package com.auto.series.Service.impl;

import com.auto.series.Event.ExamSubmittedEvent;
import com.auto.series.Event.SerieCreatedEvent;
import com.auto.series.Event.SerieDeletedEvent;
import com.auto.series.Event.SerieUpdatedEvent;
import com.auto.series.Service.SerieEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SerieEventProducerImpl implements SerieEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.serie-created}")
    private String serieCreatedTopic;

    @Value("${app.kafka.topics.serie-updated}")
    private String serieUpdatedTopic;

    @Value("${app.kafka.topics.serie-deleted}")
    private String serieDeletedTopic;

    @Value("${app.kafka.topics.exam-submitted}")
    private String examSubmittedTopic;

    @Override
    public void publishSerieCreated(String serieId, String title, String theme, String createdBy) {
        SerieCreatedEvent event = SerieCreatedEvent.builder()
                .serieId(serieId)
                .title(title)
                .theme(theme)
                .createdBy(createdBy)
                .occurredAt(LocalDateTime.now())
                .build();
        send(serieCreatedTopic, serieId, event);
    }

    @Override
    public void publishSerieUpdated(String serieId, String modifiedBy) {
        SerieUpdatedEvent event = SerieUpdatedEvent.builder()
                .serieId(serieId)
                .modifiedBy(modifiedBy)
                .occurredAt(LocalDateTime.now())
                .build();
        send(serieUpdatedTopic, serieId, event);
    }

    @Override
    public void publishSerieDeleted(String serieId, String deletedBy) {
        SerieDeletedEvent event = SerieDeletedEvent.builder()
                .serieId(serieId).deletedBy(deletedBy)
                .occurredAt(LocalDateTime.now())
                .build();
        send(serieDeletedTopic, serieId, event);
    }

    @Override
    public void publishExamSubmitted(
            String examId, String userId, String examType,
            int totalQuestions, int correctCount, double score, boolean passed,
            List<ExamSubmittedEvent.AnsweredQuestion> answers
    ) {
        ExamSubmittedEvent event = ExamSubmittedEvent.builder()
                .examId(examId).userId(userId).examType(examType)
                .totalQuestions(totalQuestions).correctCount(correctCount)
                .score(score).passed(passed).answers(answers)
                .occurredAt(LocalDateTime.now())
                .build();
        send(examSubmittedTopic, userId, event);
    }

    private void send(String topic, String key, Object event){
        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Échec de publication sur {} pour key={}", topic, key, ex);
                    }else {
                        log.info("Événement publié sur {} pour key={}", topic, key);
                    }
                });
    }
}
