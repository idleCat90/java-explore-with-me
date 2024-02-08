package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationRequest;

public interface AdminCompilationService {

    CompilationDto createCompilation(CompilationRequest compilationRequest);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, CompilationRequest compilationRequest);
}
