package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.Constants;
import ru.practicum.ewm.HitDto;
import ru.practicum.ewm.StatsRequestDto;
import ru.practicum.ewm.StatsResponseDto;
import ru.practicum.ewm.service.StatsService;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StatsController {
    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(code = HttpStatus.CREATED)
    public HitDto hit(@RequestBody HitDto hit) {
        log.info("POST \"/hit\" Body={}", hit);
        return service.saveHit(hit);
    }

    @GetMapping("/stats")
    public List<StatsResponseDto> getStats(
            @RequestParam @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT) LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT) LocalDateTime end,
            @RequestParam(defaultValue = "") List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique) {

        log.info("GET \"/stats\" start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        if (end.isBefore(start)) {
            log.error("Start must be before end");
            throw new InvalidParameterException("Start must be before end");
        }
        return service.readStats(
                StatsRequestDto.builder()
                        .start(start)
                        .end(end)
                        .uris(uris)
                        .unique(unique)
                        .build()
        );
    }
}
