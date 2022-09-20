package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UsersDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
class UsersController {

    private final UsersClient usersClient;

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody UsersDto user) {

        log.info("add " + user);
        return usersClient.add(user);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("GetMapping get all");
        return usersClient.getAll();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> patchUpdate(@PathVariable long userId, @RequestBody UsersDto userDto) {

        log.info("update userID " + userId + " userDto " + userDto);
        return usersClient.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable long userId) {

        log.info("get user id " + userId);
        return usersClient.getByID(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable long userId) {

        log.info("delete user id " + userId);
        return usersClient.deleteUser(userId);
    }
}
