package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.userSevice.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * // TODO .
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto add(@Valid @RequestBody UserDto user) {
        return userService.add(user);

    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @PatchMapping("/{userId}")
    public UserDto patchUpdate(@PathVariable long userId, @RequestBody UserDtoUpdate userDto) {
        return userService.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable long userId){
        return userService.get(userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId){
        userService.delete(userId);
    }
}
