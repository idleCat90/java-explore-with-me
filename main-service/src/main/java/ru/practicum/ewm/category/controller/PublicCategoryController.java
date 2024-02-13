package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.PublicCategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/categories")
@Validated
@RequiredArgsConstructor
@Slf4j
public class PublicCategoryController {
    private final PublicCategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> readAllCategories(@RequestParam(defaultValue = "0") @PositiveOrZero
                                                               Integer from,
                                                               @RequestParam(defaultValue = "10") @Positive
                                                               Integer size) {
        log.debug("GET /categories");
        return ResponseEntity.ok(categoryService.readAllCategories(from, size));
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> readCategoryById(@PathVariable Long catId) {
        log.debug("GET /categories/{}", catId);
        return ResponseEntity.ok(categoryService.readCategoryById(catId));
    }
}
