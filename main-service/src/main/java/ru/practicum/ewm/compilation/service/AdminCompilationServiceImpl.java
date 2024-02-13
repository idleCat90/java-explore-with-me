package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationMapper;
import ru.practicum.ewm.compilation.dto.CompilationRequest;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AdminCompilationServiceImpl implements AdminCompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto createCompilation(CompilationRequest compilationRequest) {
        log.debug("Method call: createCompilation(), request={}", compilationRequest);
        if (compilationRepository.existsByTitle(compilationRequest.getTitle())) {
            log.error("Compilation already exists with title={}", compilationRequest.getTitle());
            throw new ConflictException("Duplicate title");
        }
        Set<Event> events = (compilationRequest.getEvents() != null && !compilationRequest.getEvents().isEmpty())
                ? new HashSet<>(eventRepository.findAllById(compilationRequest.getEvents()))
                : new HashSet<>();
        Compilation compilation = Compilation.builder()
                .title(compilationRequest.getTitle())
                .pinned(compilationRequest.getPinned() != null && compilationRequest.getPinned())
                .events(events)
                .build();
        CompilationDto result = CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
        log.debug("Returned: dto={}", result);
        return result;
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        log.debug("Method call: deleteCompilation(), id={}", compId);
        if (!compilationRepository.existsById(compId)) {
            log.error("Compilation with id={} does not exist", compId);
            throw new NotFoundException("No compilation found with id=" + compId);
        }
        compilationRepository.deleteById(compId);
        log.debug("Compilation deleted");
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, CompilationRequest compilationRequest) {
        log.debug("Method call: updateCompilation(), id={}, request={}", compId, compilationRequest);
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> {
            log.error("Compilation with id={} does not exist", compId);
            return new NotFoundException("No compilation found with id=" + compId);
        });
        if (compilationRequest.getTitle() != null) {
            compilation.setTitle(compilationRequest.getTitle());
        }
        if (compilationRequest.getPinned() != null) {
            compilation.setPinned(compilationRequest.getPinned());
        }
        if (compilationRequest.getEvents() != null) {
            HashSet<Event> events = new HashSet<>(eventRepository.findAllById(compilationRequest.getEvents()));
            compilation.setEvents(events);
        }
        CompilationDto result = CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
        log.debug("Returned: dto={}", result);
        return result;
    }
}
