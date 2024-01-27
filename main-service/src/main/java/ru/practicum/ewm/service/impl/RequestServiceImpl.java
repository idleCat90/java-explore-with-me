package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.IncorrectParameterException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.mapper.RequestMapper;
import ru.practicum.ewm.model.state.EventState;
import ru.practicum.ewm.model.state.RequestStatus;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto addNewRequest(Long userId, Long eventId) {
        User user = findUser(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("No event found with id=" + eventId));
        LocalDateTime created = LocalDateTime.now();
        validateRequest(event, userId, eventId);

        Request request = Request.builder()
                .created(created)
                .requester(user)
                .event(event).build();

        if (event.isRequestModeration() && event.getParticipantLimit() != 0) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByUserId(Long userId) {
        findUser(userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        findUser(userId);
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(() ->
                new NotFoundException("No request found with id=" + requestId));
        if (request.getStatus().equals(RequestStatus.CANCELED) || request.getStatus().equals(RequestStatus.REJECTED)) {
            throw new IncorrectParameterException("Request is cancelled or rejected");
        }
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    private void validateRequest(Event event, Long userId, Long eventId) {
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Used with id=" + userId + " is event initiator");
        }
        if (event.getParticipantLimit() > 0
                && event.getParticipantLimit() <= requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)) {
            throw new ConflictException("Participant limit exceeded");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Event is not published");
        }
        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException("Request already exists");
        }
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("No user found with id=" + userId));
    }
}
