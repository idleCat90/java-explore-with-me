package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.state.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    boolean existsByIdAndInitiatorId(Long eventId, Long userId);

    boolean existsByCategoryId(Long catId);

    @Query("select e from Event e " +
            "where (e.initiator.id in ?1 or ?1 is null) " +
            "and (e.state in ?2 or ?2 is null) " +
            "and (e.category.id in ?3 or ?3 is null) " +
            "and (e.eventDate > ?4 or cast(?4 as timestamp) is null) " +
            "and (e.eventDate < ?5 or cast(?5 as timestamp) is null) ")
    List<Event> findAllAdmin(List<Long> users,
                             List<EventState> states,
                             List<Long> categories,
                             LocalDateTime rangeStart,
                             LocalDateTime rangeEnd,
                             Pageable pageable);

    @Query("select e from Event e " +
            "where ((?1 is null) or ((lower(e.annotation) like concat('%', lower(?1), '%')) " +
            "or (lower(e.description) like concat('%', lower(?1), '%')))) " +
            "and (e.category.id in ?2 or ?2 is null) " +
            "and (e.paid = ?3 or ?3 is null) " +
            "and (e.eventDate > ?4 or cast(?4 as timestamp) is null) " +
            "and (e.eventDate < ?5 or cast(?5 as timestamp) is null) " +
            "and (?6 = false or ((?6 = true and e.participantLimit > (select count(*) from Request as r where e.id = r.event.id))) " +
            "or (e.participantLimit > 0 )) ")
    List<Event> findAllPublic(String text,
                              List<Long> categories,
                              Boolean paid,
                              LocalDateTime rangeStart,
                              LocalDateTime rangeEnd,
                              Boolean onlyAvailable,
                              String sort,
                              Pageable pageable);
}
