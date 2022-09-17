package ru.practicum.server.user.userSevice;

import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.dto.UserDtoUpdate;

import java.util.List;

public interface UserService {
    UserDto add(UserDto userDto);

    List<UserDto> getAll();

    UserDto update(Long id, UserDtoUpdate userDto);

    UserDto get(Long id);

    void delete(long userId);
}
