package ru.practicum.server.user;

import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.model.User;

public class MapperUserDto {

    public static UserDto toUserDto(User user) {

        return UserDto.builder().id(user.getId()).name(user.getName()).email(user.getEmail()).build();

    }

    public static User toUser(UserDto userDto) {

        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        return user;
    }

}
