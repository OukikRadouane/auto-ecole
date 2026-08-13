package com.auto.tutorial.mapper;

import com.auto.tutorial.dto.response.CategoryResponse;
import com.auto.tutorial.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

   // @Mapping(target = "tutorials", ignore = true)
    CategoryResponse toResponse(Category category);
}