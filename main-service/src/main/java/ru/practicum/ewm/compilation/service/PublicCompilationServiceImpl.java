package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.utility.Util;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationServiceImpl implements PublicCompilationService {
    private final CompilationRepository compilationRepository;

    @Override
    public CompilationDto readCompilationById(Long compId) {
        log.debug("Method call: readCompilationById(), id={}", compId);
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> {
            log.error("Compilation with id={} does not exist", compId);
            return new NotFoundException("No compilation found with id=" + compId);
        });
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation);
        log.debug("Returned: dto={}", compilationDto);
        return compilationDto;
    }

    @Override
    public List<CompilationDto> readAllCompilations(Boolean pinned, Integer from, Integer size) {
        log.debug("Method call: readAllCompilations(), pinned={}", pinned);
        Pageable pageable = Util.getPageRequestAsc("id", from, size);
        List<Compilation> compilations = (pinned == null)
                ? compilationRepository.findAll(pageable).getContent()
                : compilationRepository.findAllByPinned(pinned, pageable);
        List<CompilationDto> compilationDtoList = CompilationMapper.toCompilationDtoList(compilations);
        log.debug("Returned: dto list, size={}", compilationDtoList.size());
        return compilationDtoList;
    }
}
