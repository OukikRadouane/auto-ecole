package com.auto.tutorial.service;

import com.auto.tutorial.dto.response.CategoryResponse;
import com.auto.tutorial.entity.Tutorial;
import com.auto.tutorial.mapper.CategoryMapper;
import com.auto.tutorial.repository.CategoryRepository;
import com.auto.tutorial.repository.TutorialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final TutorialRepository tutorialRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Cacheable("categories")
    public List<CategoryResponse> getAllCategories() {
        log.debug("Récupération de toutes les catégories");
        return categoryRepository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> getCategoriesWithPublishedTutorials() {
        log.debug("Récupération des catégories avec tutoriels publiés");
        return categoryRepository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .filter(category -> tutorialRepository.countByStatus(Tutorial.Status.PUBLISHED) > 0)
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }
}