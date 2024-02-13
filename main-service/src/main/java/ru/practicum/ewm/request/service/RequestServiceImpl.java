package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.state.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.ConfirmedRequestCountDto;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.dto.RequestMapper;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public RequestDto createRequest(Long userId, Long eventId) {
        log.debug("Method call: createRequest(), userId={}, eventId={}", userId, eventId);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User with id={} does not exist", userId);
            return new NotFoundException("No user found with id=" + userId);
        });

        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event with id={} does not exist", eventId);
            return new NotFoundException("No event found with id=" + eventId);
        });

        if (event.getInitiator().getId().equals(userId)) {
            log.error("Event request from initiator");
            throw new ConflictException("Event initiator can not send participation requests");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            log.error("Event is not PUBLISHED");
            throw new ConflictException("Event must be published to create requests");
        }

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            log.error("Request already exists");
            throw new ConflictException("Request has been made already");
        }

        RequestStatus status = (!event.getRequestModeration() || event.getParticipantLimit() == 0)
                ? RequestStatus.CONFIRMED
                : RequestStatus.PENDING;

        List<ConfirmedRequestCountDto> confirmedCount = requestRepository.countByEventId(List.of(eventId));
        if (confirmedCount.size() >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            log.error("Participant limit exceeded");
            throw new ConflictException("Participant limit exceeded for event with id=" + eventId);
        }

        Request request = requestRepository.save(Request.builder()
                .requester(user)
                .event(event)
                .created(LocalDateTime.now())
                .status(status)
                .build());
        log.debug("Returned request={}", request);
        return RequestMapper.toRequestDto(request);
    }

    @Override
    public List<RequestDto> readAllRequests(Long userId) {
        log.debug("Method call: readAllRequests(), userId={}", userId);
        List<RequestDto> requests = RequestMapper.toRequestDtoList(requestRepository.findAllByRequesterId(userId));
        log.debug("Returned: requests={}", requests);
        return requests;
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        log.debug("Method call: cancelRequest(), userId={}, requestId={}", userId, requestId);
        if (!userRepository.existsById(userId)) {
            log.error("User with id={} does not exist", userId);
            throw new NotFoundException("No user found with id=" + userId);
        }
        Request request = requestRepository.findById(requestId).orElseThrow(() -> {
            log.error("Request with id={} does not exist", requestId);
            return new NotFoundException("No request found with id=" + requestId);
        });

        request.setStatus(RequestStatus.CANCELED);
        log.debug("Returned: requestId={}, status={}", requestId, request.getStatus());
        return RequestMapper.toRequestDto(request);
    }
}
