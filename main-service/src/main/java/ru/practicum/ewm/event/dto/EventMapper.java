package ru.practicum.ewm.event.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.location.dto.LocationMapper;
import ru.practicum.ewm.user.dto.mapper.UserMapper;
import ru.practicum.ewm.utility.Util;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class EventMapper {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(Util.DATE_TIME_FORMAT);

    public Event toEvent(EventRequestDto eventRequestDto) {
        return Event.builder()
                .annotation(eventRequestDto.getAnnotation())
                .category(Category.builder().id(eventRequestDto.getCategory()).build())
                .description(eventRequestDto.getDescription())
                .eventDate(eventRequestDto.getEventDate())
                .location(LocationMapper.toLocation(eventRequestDto.getLocation()))
                .paid(eventRequestDto.isPaid())
                .participantLimit(eventRequestDto.getParticipantLimit())
                .requestModeration(eventRequestDto.isRequestModeration())
                .title(eventRequestDto.getTitle())
                .build();
    }

    public EventFullDto toEventFullDto(Event event) {
        EventFullDto result = EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().format(DATE_TIME_FORMATTER))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(DATE_TIME_FORMATTER))
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .commentCount(event.getCommentCount())
                .build();
        if (event.getPublishedOn() != null) {
            result.setPublishedOn(event.getPublishedOn().format(DATE_TIME_FORMATTER));
        }
        return result;
    }

    public List<EventFullDto> toEventFullDtoList(List<Event> events) {
        return events.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate().format(DATE_TIME_FORMATTER))
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .commentCount(event.getCommentCount())
                .build();
    }

    public List<EventShortDto> toEventShortDtoList(List<Event> events) {
        return events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    public EventCommentDto toEventCommentDto(Event event) {
        return EventCommentDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .eventDate(event.getEventDate().format(DATE_TIME_FORMATTER))
                .build();
    }
}
