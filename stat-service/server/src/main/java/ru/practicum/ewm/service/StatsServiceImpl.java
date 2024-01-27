package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.HitDto;
import ru.practicum.ewm.StatsRequestDto;
import ru.practicum.ewm.StatsResponseDto;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.repository.StatsRepository;

import java.util.List;

import static ru.practicum.ewm.model.HitMapper.toHit;
import static ru.practicum.ewm.model.HitMapper.toHitDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public HitDto saveHit(HitDto hitDto) {
        log.debug("saveHit() with dto: {}", hitDto);
        Hit hit = statsRepository.save(toHit(hitDto));
        return toHitDto(hit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatsResponseDto> readStats(StatsRequestDto requestDto) {
        log.debug("readStats() with dto: {}", requestDto);
        boolean noUris = requestDto.getUris().isEmpty();

        if (requestDto.getUnique()) {
            if (noUris) {
                return statsRepository.findAllByTimestampBetweenStartAndEndWithUniqueIp(
                                requestDto.getStart(),
                                requestDto.getEnd()
                        );
            } else {
                return statsRepository.findAllByTimestampBetweenStartAndEndByUriWithUniqueIp(
                                requestDto.getStart(),
                                requestDto.getEnd(),
                                requestDto.getUris()
                        );
            }
        } else {
            if (noUris) {
                return statsRepository.findAllByTimestampBetweenStartAndEnd(
                                requestDto.getStart(),
                                requestDto.getEnd()
                        );
            } else {
                return statsRepository.findAllByTimestampBetweenStartAndEndByUri(
                                requestDto.getStart(),
                                requestDto.getEnd(),
                                requestDto.getUris()
                        );
            }
        }
    }
}
