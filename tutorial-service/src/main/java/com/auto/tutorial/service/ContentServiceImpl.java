package com.auto.tutorial.service;

import com.auto.tutorial.dto.request.ContentCreateRequest;
import com.auto.tutorial.dto.request.ContentUpdateRequest;
import com.auto.tutorial.dto.response.ContentResponse;
import com.auto.tutorial.entity.Content;
import com.auto.tutorial.entity.Tutorial;
import com.auto.tutorial.exception.ResourceNotFoundException;
import com.auto.tutorial.mapper.ContentMapper;
import com.auto.tutorial.repository.ContentRepository;
import com.auto.tutorial.repository.TutorialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContentServiceImpl implements ContentService {

    private final ContentRepository contentRepository;
    private final TutorialRepository tutorialRepository;
    private final ContentMapper contentMapper;

    @Override
    public ContentResponse createContent(String tutorialId, ContentCreateRequest request) {
        log.info("Création d'un contenu pour le tutoriel : {}", tutorialId);

        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new ResourceNotFoundException("Tutoriel non trouvé : " + tutorialId));

        Content content = contentMapper.toEntity(request);
        content.setContentType(Content.ContentType.valueOf(request.getContentType()));
        content.setTutorial(tutorial);

        if (content.getDisplayOrder() == null) {
            long count = contentRepository.countByTutorialId(tutorialId);
            content.setDisplayOrder((int) count);
        }

        Content saved = contentRepository.save(content);
        log.info("Contenu créé avec l'ID : {}", saved.getId());

        return contentMapper.toResponse(saved);
    }

    @Override
    public ContentResponse updateContent(String contentId, ContentUpdateRequest request) {
        log.info("Mise à jour du contenu : {}", contentId);

        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Contenu non trouvé : " + contentId));

        if (request.getTitle() != null) content.setTitle(request.getTitle());
        if (request.getDescription() != null) content.setDescription(request.getDescription());
        if (request.getDisplayOrder() != null) content.setDisplayOrder(request.getDisplayOrder());
        if (request.getIsRequired() != null) content.setIsRequired(request.getIsRequired());

        // Spécifique vidéo
        if (request.getDuration() != null) content.setDuration(request.getDuration());
        if (request.getThumbnail() != null) content.setThumbnail(request.getThumbnail());

        // Spécifique PDF
        if (request.getPageCount() != null) content.setPageCount(request.getPageCount());

        Content updated = contentRepository.save(content);
        log.info("Contenu mis à jour : {}", contentId);

        return contentMapper.toResponse(updated);
    }

    @Override
    public void deleteContent(String contentId) {
        log.info("Suppression du contenu : {}", contentId);

        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Contenu non trouvé : " + contentId));

        contentRepository.delete(content);
        log.info("Contenu supprimé : {}", contentId);
    }

    @Override
    public ContentResponse getContent(String contentId) {
        log.debug("Récupération du contenu : {}", contentId);

        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Contenu non trouvé : " + contentId));

        return contentMapper.toResponse(content);
    }

    @Override
    public List<ContentResponse> getContentsByTutorial(String tutorialId) {
        log.debug("Récupération des contenus du tutoriel : {}", tutorialId);

        return contentRepository.findByTutorialIdOrderByDisplayOrderAsc(tutorialId)
                .stream()
                .map(contentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void reorderContents(String tutorialId, List<String> contentIds) {
        log.info("Réorganisation des contenus pour le tutoriel : {}", tutorialId);

        if (!tutorialRepository.existsById(tutorialId)) {
            throw new ResourceNotFoundException("Tutoriel non trouvé : " + tutorialId);
        }

        int order = 0;
        for (String contentId : contentIds) {
            Content content = contentRepository.findById(contentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Contenu non trouvé : " + contentId));
            content.setDisplayOrder(order++);
            contentRepository.save(content);
        }

        log.info("Contenus réorganisés pour le tutoriel : {}", tutorialId);
    }
}