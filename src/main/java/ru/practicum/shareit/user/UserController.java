package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;
import ru.practicum.shareit.user.userSevice.UserServiceImp;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserServiceImp userService;

    public UserController(UserServiceImp userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto add(@Valid @RequestBody UserDto user) {

        log.info("add " + user);
        return userService.add(user);
    }

    @GetMapping
    public List<UserDto> getAll() {

        log.info("GetMapping get all");
        return userService.getAll();
    }

    @PatchMapping("/{userId}")
    public UserDto patchUpdate(@PathVariable long userId, @RequestBody UserDtoUpdate userDto) {

        log.info("update userID " + userId + " userDto " + userDto);
        return userService.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable long userId) {

        log.info("get user id " + userId);
        return userService.get(userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {

        log.info("delete user id " + userId);
        userService.delete(userId);
    }
}
