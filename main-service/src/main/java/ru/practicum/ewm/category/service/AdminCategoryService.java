package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;

public interface AdminCategoryService {

    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    void deleteCategory(Long catId);
}
