package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        log.debug("Method call: createCategory(), categoryDto={}", categoryDto);
        if (categoryRepository.existsByName(categoryDto.getName())) {
            log.error("Not unique category name: {}", categoryDto.getName());
            throw new ConflictException("Category with that name already exists");
        }
        try {
            Category category = categoryRepository.saveAndFlush(CategoryMapper.toCategory(categoryDto));
            log.debug("Returned saved category: {}", category);
            return CategoryMapper.toCategoryDto(category);
        } catch (DataIntegrityViolationException e) {
            log.error("Category already exists: {}", categoryDto);
            throw new ConflictException("Category already exists");
        }
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        log.debug("Method call: updateCategory(), id={}", catId);
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("No category found with id=" + catId));
        if (!category.getName().equals(categoryDto.getName()) &&
                categoryRepository.existsByName(categoryDto.getName())) {
            log.error("Not unique category name: {}", categoryDto.getName());
            throw new ConflictException("Category with that name already exists");
        }
        category.setName(categoryDto.getName());
        log.debug("Returned updated category: {}", CategoryMapper.toCategoryDto(category));
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        log.debug("Method call: deleteCategory(), id={}", catId);
        if (!categoryRepository.existsById(catId)) {
            log.error("Category with id={} does not exist", catId);
            throw new NotFoundException("No category found with id=" + catId);
        }
        if (eventRepository.existsByCategoryId(catId)) {
            log.error("Category with id={} has associated events", catId);
            throw new ConflictException("Category has associated events, can not be deleted");
        }
        categoryRepository.deleteById(catId);
        log.debug("Category with id={} deleted", catId);
    }
}
