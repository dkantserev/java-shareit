package ru.practicum.server.shareit.user.userSevice;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.server.user.MapperUserDto;
import ru.practicum.server.user.UserExceptions.UserNotFound;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.dto.UserDtoUpdate;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.storage.UserStorage;
import ru.practicum.server.user.userSevice.UserServiceImp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {
    @Mock
    UserStorage userStorage;

    @InjectMocks
    UserServiceImp userServiceImp;

    User user = new User();

    @Test
    public void whenGet_ThenPositive() { //объект найден

        user.setId(1L);
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.ofNullable(user));
        assertEquals(userServiceImp.get(1L), MapperUserDto.toUserDto(user));
    }

    @Test
    public void whenGet_ThenNegative() { //объект ненайден - ошибка

        user.setId(1L);
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFound.class, () -> userServiceImp.get(1L));
    }

    @Test
    public void whenADD_ThenPositive() { //объект добавлен

        user.setId(1L);
        UserDto userDto1 = MapperUserDto.toUserDto(user);
        try (MockedStatic<MapperUserDto> utilities = Mockito.mockStatic(MapperUserDto.class)) {
            utilities.when(() -> MapperUserDto.toUserDto(user)).thenReturn(userDto1);
            utilities.when(() -> MapperUserDto.toUser(userDto1)).thenReturn(user);
            userServiceImp.add(userDto1);
            Mockito.verify(userStorage, Mockito.times(1)).save(user);
        }

    }

    @Test
    public void whenGetAll_ThenPositive() { //список объектов найден

        user.setId(1L);
        UserDto userDto1 = MapperUserDto.toUserDto(user);
        try (MockedStatic<MapperUserDto> utilities = Mockito.mockStatic(MapperUserDto.class)) {
            utilities.when(() -> MapperUserDto.toUserDto(user)).thenReturn(userDto1);
            utilities.when(() -> MapperUserDto.toUser(userDto1)).thenReturn(user);
            List<User> all = new ArrayList<>();
            List<UserDto> all1 = new ArrayList<>();
            all1.add(userDto1);
            all.add(user);
            Mockito
                    .when(userStorage.findAll())
                    .thenReturn(all);
            assertEquals(userServiceImp.getAll(), all1);
        }
    }

    @Test
    public void whenDelete_ThenPositive() { //  объект удален

        userServiceImp.delete(1L);
        Mockito.verify(userStorage, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void whenUpdate_ThenPositive() { //объект обнавлен

        user.setId(1L);
        user.setName("t");
        user.setEmail("tt@mail.ru");
        UserDto userDto1 = MapperUserDto.toUserDto(user);
        UserDtoUpdate userDtoUpdate = UserDtoUpdate.builder().name("t").email("tt@mail.ru").build();

        try (MockedStatic<MapperUserDto> utilities = Mockito.mockStatic(MapperUserDto.class)) {
            utilities.when(() -> MapperUserDto.toUserDto(user)).thenReturn(userDto1);
            utilities.when(() -> MapperUserDto.toUser(userDto1)).thenReturn(user);
            Mockito
                    .when(userStorage.saveAndFlush(user))
                    .thenReturn(user);
            Mockito
                    .when(userStorage.findById(1L))
                    .thenReturn(Optional.ofNullable(user));
            userServiceImp.update(1L, userDtoUpdate);
            Mockito.verify(userStorage, Mockito.times(1)).findById(1L);
            Mockito.verify(userStorage, Mockito.times(1)).saveAndFlush(user);
        }
    }

    @Test
    public void whenUpdate_ThenNegative() { //объект не найден

        user.setId(1L);
        user.setName("t");
        user.setEmail("tt@mail.ru");
        UserDto userDto1 = MapperUserDto.toUserDto(user);
        UserDtoUpdate userDtoUpdate = UserDtoUpdate.builder().build();
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFound.class, () -> userServiceImp.update(1L, userDtoUpdate));

    }

}