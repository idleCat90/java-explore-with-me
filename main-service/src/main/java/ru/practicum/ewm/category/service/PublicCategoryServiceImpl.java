package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.utility.Util;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PublicCategoryServiceImpl implements PublicCategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> readAllCategories(Integer from, Integer size) {
        log.debug("Method call: readAllCategories()");
        List<Category> categories = categoryRepository
                .findAll(Util.getPageRequestAsc("id", from, size))
                .getContent();
        log.debug("Returned categories, size={}", categories.size());
        return CategoryMapper.toCategoryDtoList(categories);
    }

    @Override
    public CategoryDto readCategoryById(Long catId) {
        log.debug("Method call: readCategoryById(), id={}", catId);
        Category category = categoryRepository.findById(catId).orElseThrow(() -> {
            log.error("Category with id={} does not exist", catId);
            return new NotFoundException("No category found with id=" + catId);
        });
        log.debug("Returned category: {}", category.getName());
        return CategoryMapper.toCategoryDto(category);
    }
}
