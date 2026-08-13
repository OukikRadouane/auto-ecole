package com.auto.tutorial.service;

import com.auto.tutorial.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    List<CategoryResponse> getCategoriesWithPublishedTutorials();
}