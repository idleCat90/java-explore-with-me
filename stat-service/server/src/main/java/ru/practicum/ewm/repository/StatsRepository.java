package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.StatsResponseDto;
import ru.practicum.ewm.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {
    @Query("select new ru.practicum.ewm.StatsResponseDto(hit.app, hit.uri, count(distinct hit.ip)) " +
            "from Hit as hit " +
            "where hit.timestamp between ?1 and ?2 " +
            "group by hit.app, hit.uri " +
            "order by count(distinct hit.ip) desc ")
    List<StatsResponseDto> findAllByTimestampBetweenStartAndEndWithUniqueIp(LocalDateTime start,
                                                                            LocalDateTime end);

    @Query("select new ru.practicum.ewm.StatsResponseDto(hit.app, hit.uri, count(distinct hit.ip)) " +
            "from Hit as hit " +
            "where hit.timestamp between ?1 and ?2 and hit.uri in ?3 " +
            "group by hit.app, hit.uri " +
            "order by count(distinct hit.ip) desc ")
    List<StatsResponseDto> findAllByTimestampBetweenStartAndEndByUriWithUniqueIp(LocalDateTime start,
                                                                                 LocalDateTime end,
                                                                                 List<String> uris);

    @Query("select new ru.practicum.ewm.StatsResponseDto(hit.app, hit.uri, count(hit.ip)) " +
            "from Hit as hit " +
            "where hit.timestamp between ?1 and ?2 " +
            "group by hit.app, hit.uri " +
            "order by count(hit.ip) desc ")
    List<StatsResponseDto> findAllByTimestampBetweenStartAndEnd(LocalDateTime start,
                                                                LocalDateTime end);

    @Query("select new ru.practicum.ewm.StatsResponseDto(hit.app, hit.uri, count(hit.ip)) " +
            "from Hit as hit " +
            "where hit.timestamp between ?1 and ?2 and hit.uri in ?3 " +
            "group by hit.app, hit.uri " +
            "order by count(hit.ip) desc ")
    List<StatsResponseDto> findAllByTimestampBetweenStartAndEndByUri(LocalDateTime start,
                                                                     LocalDateTime end,
                                                                     List<String> uris);

}
