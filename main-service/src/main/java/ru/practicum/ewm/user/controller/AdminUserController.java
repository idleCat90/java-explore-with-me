package ru.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.mapper.UserMapper;
import ru.practicum.ewm.user.service.UserService;
import ru.practicum.ewm.utility.Util.Marker.OnCreate;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@Validated
@Slf4j
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDto> createUser(@RequestBody @Validated({OnCreate.class}) NewUserRequest newUserRequest) {
        log.info("POST /admin/users with body={}", newUserRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(UserMapper.toUser(newUserRequest)));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam(defaultValue = "") List<Long> ids,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("GET /admin/users with ids={}", ids);
        return ResponseEntity.ok(UserMapper.toUserDtoList(userService.readUsers(ids, from, size)));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        log.info("DELETE /admin/users/{}", userId);
        userService.deleteUser(userId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("User with id=" + userId + "deleted");
    }
}
