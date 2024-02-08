package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface UserService {

    UserDto createUser(User user);

    List<User> readUsers(List<Long> ids, Integer from, Integer size);

    void deleteUser(Long userId);
}
