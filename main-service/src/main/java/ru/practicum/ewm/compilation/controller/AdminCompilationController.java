package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationRequest;
import ru.practicum.ewm.compilation.service.AdminCompilationService;
import ru.practicum.ewm.utility.Util.Marker.onCreate;
import ru.practicum.ewm.utility.Util.Marker.onUpdate;

@RestController
@RequestMapping("/admin/compilations")
@Validated
@RequiredArgsConstructor
@Slf4j
public class AdminCompilationController {
    private final AdminCompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CompilationDto> postCompilation(@RequestBody @Validated(onCreate.class)
                                                          CompilationRequest compilationRequest) {
        log.debug("POST /admin/compilations, dto={}", compilationRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(compilationService.createCompilation(compilationRequest));
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> patchCompilation(@PathVariable Long compId,
                                                           @RequestBody @Validated(onUpdate.class)
                                                           CompilationRequest compilationRequest) {
        log.debug("PATCH /admin/compilations/{}, dto={}", compId, compilationRequest);
        return ResponseEntity.ok(compilationService.updateCompilation(compId, compilationRequest));
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deleteCompilation(@PathVariable Long compId) {
        log.debug("DELETE /admin/compilations/{}", compId);
        compilationService.deleteCompilation(compId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("Compilation deleted with id=" + compId);
    }
}
