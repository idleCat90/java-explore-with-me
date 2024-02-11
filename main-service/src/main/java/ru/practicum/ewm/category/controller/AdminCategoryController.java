package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.AdminCategoryService;
import ru.practicum.ewm.utility.Util.Marker.*;

@RestController
@RequestMapping("/admin/categories")
@Validated
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryController {
    private final AdminCategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CategoryDto> addCategory(@RequestBody @Validated(OnCreate.class)
                                                   CategoryDto categoryDto,
                                                   BindingResult bindingResult) {
        log.debug("POST /admin/categories with body={}", categoryDto);
        if (bindingResult.hasErrors()) {
            log.error("Category validation error");
            return ResponseEntity.badRequest().body(categoryDto);
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.createCategory(categoryDto));
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long catId,
                                                      @RequestBody @Validated(OnUpdate.class) CategoryDto categoryDto) {
        log.debug("PATCH /admin/categories/{} with body={}", catId, categoryDto);
        return ResponseEntity
                .ok(categoryService.updateCategory(catId, categoryDto));
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deleteCategory(@PathVariable Long catId) {
        log.debug("DELETE /admin/categories/{}", catId);
        categoryService.deleteCategory(catId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("Category with id=" + catId + " deleted");
    }
}
