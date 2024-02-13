package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;

import java.util.List;

public interface PublicCategoryService {

    List<CategoryDto> readAllCategories(Integer from, Integer size);

    CategoryDto readCategoryById(Long catId);
}
