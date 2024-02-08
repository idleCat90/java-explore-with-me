package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.request.dto.ConfirmedRequestCountDto;
import ru.practicum.ewm.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllByEventId(Long eventId);

    @Query("select new ru.practicum.ewm.request.dto.ConfirmedRequestCountDto(r.event.id, count(r.id)) " +
            "from Request r " +
            "where r.event.id in ?1 " +
            "and r.status = 'CONFIRMED' " +
            "group by r.event.id ")
    List<ConfirmedRequestCountDto> countByEventId(List<Long> ids);

    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);
}
