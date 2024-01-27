package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.mapper.CompilationMapper;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.CompilationService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        compilation.setPinned(Optional.ofNullable(compilation.getPinned()).orElse(false));
        Set<Long> eventIds = (newCompilationDto.getEvents() != null)
                ? newCompilationDto.getEvents()
                : Collections.emptySet();
        List<Event> events = eventRepository.findAllByIdIn(new ArrayList<>(eventIds));
        compilation.setEvents(new HashSet<>(events));

        Compilation savedCompilation = compilationRepository.save(compilation);
        return CompilationMapper.toDto(savedCompilation);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Compilation> compilations = (pinned == null)
                ? compilationRepository.findAll(pageRequest).getContent()
                : compilationRepository.findAllByPinned(pinned, pageRequest);

        return compilations.stream()
                .map(CompilationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long id) {
        return CompilationMapper.toDto(findCompilation(id));
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long id, UpdateCompilationDto updateCompilationDto) {
        Compilation compilation = findCompilation(id);
        Set<Long> eventIds = updateCompilationDto.getEvents();
        if (eventIds != null) {
            List<Event> events = eventRepository.findAllByIdIn(new ArrayList<>(eventIds));
            compilation.setEvents(new HashSet<>(events));
        }
        compilation.setPinned(Optional.ofNullable(updateCompilationDto.getPinned()).orElse(compilation.getPinned()));
        compilation.setTitle(Optional.ofNullable(updateCompilationDto.getTitle()).orElse(compilation.getTitle()));
        return CompilationMapper.toDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilationById(Long id) {
        findCompilation(id);
        compilationRepository.deleteById(id);
    }

    private Compilation findCompilation(Long id) {
        return compilationRepository.findById(id).orElseThrow(() ->
                new NotFoundException("No compilation with id=" + id));
    }
}
