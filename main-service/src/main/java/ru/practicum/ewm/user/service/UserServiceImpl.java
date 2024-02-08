package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.utility.Util;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(User user) {
        log.debug("Method call: createUser()");
        if (userRepository.existsByEmail(user.getEmail())) {
            log.error("Duplicate email: {}", user.getEmail());
            throw new ConflictException("User with such email already exists");
        }

        UserDto userDto = UserMapper.toUserFullDto(userRepository.save(user));
        log.debug("Returned: {}", userDto);
        return userDto;
    }

    @Override
    public List<User> readUsers(List<Long> ids, Integer from, Integer size) {
        log.debug("Method call: readUsers(), ids={}", ids);
        Pageable pageable = Util.getPageRequestAsc("id", from, size);
        if (ids.isEmpty()) {
            List<User> allUsers = userRepository.findAll(pageable).getContent();
            log.debug("Returned allUsers, size={}", allUsers.size());
            return allUsers;
        }
        List<User> users = userRepository.findAllByIdIn(ids, pageable);
        if (users.isEmpty()) {
            return List.of();
        }
        log.debug("Returned: users, size={}", users.size());
        return users;
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.debug("Method call: deleteUser(), id={}", userId);
        if (!userRepository.existsById(userId)) {
            log.error("User with id={} does not exist", userId);
            throw new NotFoundException("No user found with id=" + userId);
        }
        userRepository.deleteById(userId);
        log.debug("User with id={} removed", userId);
    }
}
