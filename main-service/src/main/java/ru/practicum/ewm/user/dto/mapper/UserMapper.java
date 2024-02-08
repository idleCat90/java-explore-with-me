package ru.practicum.ewm.user.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class UserMapper {

    public UserDto toUserFullDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }


    public UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public User toUser(NewUserRequest newUserRequest) {
        return User.builder()
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .build();
    }

    public List<UserDto> toUserDtoList(List<User> users) {
        return users.stream()
                .map(UserMapper::toUserFullDto)
                .collect(Collectors.toList());
    }
}
