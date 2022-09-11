package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class MapperUserDtoTest {

    User user = new User();
    UserDto userDto = UserDto.builder().build();

    @Test
    void toUserDto() {

        user.setId(1L);
        user.setName("t");
        assertEquals(MapperUserDto.toUserDto(user).getId(), 1L);
        assertEquals(MapperUserDto.toUserDto(user).getName(), "t");
        assertNull(MapperUserDto.toUserDto(user).getEmail());
    }

    @Test
    void toUser() {

        userDto.setId(1L);
        userDto.setName("t");
        assertEquals(MapperUserDto.toUser(userDto).getId(), 1L);
        assertEquals(MapperUserDto.toUser(userDto).getName(), "t");
        assertNull(MapperUserDto.toUser(userDto).getEmail());
    }
}