package com.auto.tutorial.service;

import com.auto.tutorial.dto.request.TutorialCreateRequest;
import com.auto.tutorial.dto.request.TutorialUpdateRequest;
import com.auto.tutorial.dto.request.ContentCreateRequest;
import com.auto.tutorial.dto.response.TutorialDetailResponse;
import com.auto.tutorial.dto.response.TutorialResponse;
import com.auto.tutorial.entity.Category;
import com.auto.tutorial.entity.Tutorial;
import com.auto.tutorial.entity.Content;
import com.auto.tutorial.exception.ResourceNotFoundException;
import com.auto.tutorial.mapper.TutorialMapper;
import com.auto.tutorial.mapper.ContentMapper;
import com.auto.tutorial.repository.CategoryRepository;
import com.auto.tutorial.repository.TutorialRepository;
import com.auto.tutorial.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TutorialServiceImpl implements TutorialService {

    private final TutorialRepository tutorialRepository;
    private final CategoryRepository categoryRepository;
    private final ContentRepository contentRepository;
    private final TutorialMapper tutorialMapper;
    private final ContentMapper contentMapper;

    // ⏸️ Événements commentés - les autres services n'existent pas encore
    // private final EventPublisherService eventPublisherService;

    @Override
    public TutorialResponse createTutorial(TutorialCreateRequest request) {
        log.info("Création d'un nouveau tutoriel : {}", request.getTitle());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée : " + request.getCategoryId()));

        Tutorial tutorial = tutorialMapper.toEntity(request);
        tutorial.setCategory(category);
        tutorial.setStatus(Tutorial.Status.DRAFT);

        if (request.getAccessType() != null) {
            tutorial.setAccessType(Tutorial.AccessType.valueOf(request.getAccessType()));
        }

        if (request.getContents() != null && !request.getContents().isEmpty()) {
            for (int i = 0; i < request.getContents().size(); i++) {
                ContentCreateRequest contentRequest = request.getContents().get(i);
                Content content = contentMapper.toEntity(contentRequest);
                content.setContentType(Content.ContentType.valueOf(contentRequest.getContentType()));
                if (content.getDisplayOrder() == null) {
                    content.setDisplayOrder(i);
                }
                tutorial.addContent(content);
            }
        }

        Tutorial saved = tutorialRepository.save(tutorial);
        log.info("Tutoriel créé avec l'ID : {}", saved.getId());

        // ⏸️ COMMENTÉ - Les autres services n'existent pas encore
        // eventPublisherService.publishTutorialCreated(saved);

        return tutorialMapper.toResponse(saved);
    }
    /*

    @Override
    @Cacheable(value = "tutorials", key = "#pageable.pageNumber + '-' + #search + '-' + #category + '-' + #difficulty")
    public Page<TutorialResponse> getPublishedTutorials(Pageable pageable, String search, String category, String difficulty) {
        log.debug("Récupération des tutoriels publiés");

        if (search == null && category == null && difficulty == null) {
            return tutorialRepository.findByStatus(Tutorial.Status.PUBLISHED, pageable)
                    .map(tutorialMapper::toResponse);
        }

        return tutorialRepository.searchPublishedTutorials(search, category, difficulty, pageable)
                .map(tutorialMapper::toResponse);
    }
*/
    @Override
    @Cacheable(value = "tutorials", key = "#pageable.pageNumber + '-' + #search + '-' + #category + '-' + #difficulty")
    public Page<TutorialResponse> getPublishedTutorials(Pageable pageable, String search, String category, String difficulty) {
        log.debug("Récupération des tutoriels publiés");

        //  Convertir difficulty en enum
        Tutorial.Difficulty difficultyEnum = null;
        if (difficulty != null && !difficulty.isEmpty()) {
            try {
                difficultyEnum = Tutorial.Difficulty.valueOf(difficulty.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Difficulté invalide : {}", difficulty);
            }
        }

        // Si aucun filtre, retourner tous les tutoriels publiés
        if (search == null && category == null && difficulty == null) {
            return tutorialRepository.findByStatus(Tutorial.Status.PUBLISHED, pageable)
                    .map(tutorialMapper::toResponse);
        }

        // Passer difficultyEnum au lieu de difficulty
        return tutorialRepository.searchPublishedTutorials(search, category, difficultyEnum, pageable)
                .map(tutorialMapper::toResponse);
    }

    @Override
    public TutorialDetailResponse getTutorialDetail(String id) {
        log.debug("Récupération du détail du tutoriel : {}", id);

        Tutorial tutorial = tutorialRepository.findByIdAndStatus(id, Tutorial.Status.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Tutoriel non trouvé : " + id));

        tutorialRepository.incrementViewCount(id);

        return tutorialMapper.toDetailResponse(tutorial);
    }

    @Override
    public List<TutorialResponse> getTutorialsByCategory(String categoryId) {
        log.debug("Récupération des tutoriels par catégorie : {}", categoryId);
        return tutorialRepository.findByCategoryIdAndStatusOrderByDisplayOrderAsc(categoryId, Tutorial.Status.PUBLISHED)
                .stream()
                .map(tutorialMapper::toResponse)
                .toList();
    }

    @Override
    public List<TutorialResponse> getPublishedTutorialsList() {
        log.debug("Récupération de tous les tutoriels publiés");
        return tutorialRepository.findByStatusOrderByCreatedAtDesc(Tutorial.Status.PUBLISHED)
                .stream()
                .map(tutorialMapper::toResponse)
                .toList();
    }

    @Override
    @CacheEvict(value = "tutorials", allEntries = true)
    public TutorialResponse updateTutorial(String id, TutorialUpdateRequest request) {
        log.info("Mise à jour du tutoriel : {}", id);

        Tutorial tutorial = tutorialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutoriel non trouvé : " + id));

        tutorialMapper.updateEntity(tutorial, request);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée : " + request.getCategoryId()));
            tutorial.setCategory(category);
        }

        if (request.getAccessType() != null) {
            tutorial.setAccessType(Tutorial.AccessType.valueOf(request.getAccessType()));
        }

        if (request.getStatus() != null) {
            tutorial.setStatus(Tutorial.Status.valueOf(request.getStatus()));
        }

        if (request.getContents() != null) {
            contentRepository.deleteByTutorialId(id);
            tutorial.getContents().clear();

            for (int i = 0; i < request.getContents().size(); i++) {
                ContentCreateRequest contentRequest = request.getContents().get(i);
                Content content = contentMapper.toEntity(contentRequest);
                content.setContentType(Content.ContentType.valueOf(contentRequest.getContentType()));
                if (content.getDisplayOrder() == null) {
                    content.setDisplayOrder(i);
                }
                tutorial.addContent(content);
            }
        }

        Tutorial updated = tutorialRepository.save(tutorial);
        log.info("Tutoriel mis à jour : {}", id);

        // ⏸️ COMMENTÉ - Les autres services n'existent pas encore
        // eventPublisherService.publishTutorialUpdated(updated);

        return tutorialMapper.toResponse(updated);
    }

    @Override
    public TutorialResponse publishTutorial(String id) {
        log.info("Publication du tutoriel : {}", id);

        Tutorial tutorial = tutorialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutoriel non trouvé : " + id));

        tutorial.setStatus(Tutorial.Status.PUBLISHED);
        tutorial.setPublishedAt(LocalDateTime.now());

        Tutorial saved = tutorialRepository.save(tutorial);
        log.info("Tutoriel publié : {}", id);

        // ⏸️ COMMENTÉ - Les autres services n'existent pas encore
        // eventPublisherService.publishTutorialPublished(saved);

        return tutorialMapper.toResponse(saved);
    }

    @Override
    public TutorialResponse archiveTutorial(String id) {
        log.info("Archivage du tutoriel : {}", id);

        Tutorial tutorial = tutorialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutoriel non trouvé : " + id));

        tutorial.setStatus(Tutorial.Status.ARCHIVED);

        Tutorial saved = tutorialRepository.save(tutorial);
        log.info("Tutoriel archivé : {}", id);

        return tutorialMapper.toResponse(saved);
    }

    @Override
    @CacheEvict(value = "tutorials", allEntries = true)
    public void deleteTutorial(String id) {
        log.info("Suppression du tutoriel : {}", id);

        Tutorial tutorial = tutorialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutoriel non trouvé : " + id));

        tutorialRepository.delete(tutorial);
        log.info("Tutoriel supprimé : {}", id);
    }

    @Override
    public Page<TutorialResponse> getAllTutorials(Pageable pageable, String search, String status) {
        log.debug("Récupération de tous les tutoriels (admin)");

        if (status != null && !status.isEmpty()) {
            try {
                Tutorial.Status statusEnum = Tutorial.Status.valueOf(status.toUpperCase());
                return tutorialRepository.findByStatus(statusEnum, pageable)
                        .map(tutorialMapper::toResponse);
            } catch (IllegalArgumentException e) {
                // Ignorer
                throw new IllegalArgumentException(
                        "Statut invalide. Les valeurs autorisées sont : " +
                                java.util.Arrays.toString(Tutorial.Status.values())
                );
            }
        }

        return tutorialRepository.findAll(pageable)
                .map(tutorialMapper::toResponse);
    }
}