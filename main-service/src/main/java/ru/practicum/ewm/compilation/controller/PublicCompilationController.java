package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.PublicCompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/compilations")
@Validated
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationController {
    private final PublicCompilationService compilationService;

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilationById(@PathVariable Long compId) {
        log.debug("GET /compilations/{}", compId);
        return ResponseEntity.ok(compilationService.readCompilationById(compId));
    }

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                                @RequestParam(defaultValue = "0") @PositiveOrZero
                                                                Integer from,
                                                                @RequestParam(defaultValue = "10") @Positive
                                                                Integer size) {
        log.debug("GET /compilations, pinned={}", pinned);
        return ResponseEntity.ok(compilationService.readAllCompilations(pinned, from, size));
    }
}
