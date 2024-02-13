package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;

import java.util.List;

public interface PublicCompilationService {

    CompilationDto readCompilationById(Long compId);

    List<CompilationDto> readAllCompilations(Boolean pinned, Integer from, Integer size);
}
