package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.userSevice.UserServiceImp;

import java.util.Optional;

public class Test {
    @org.junit.jupiter.api.Test
    public void test1() {

        User user = new User();
        user.setId(1L);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        UserServiceImp userServiceImp = new UserServiceImp(userStorage);
        Mockito
                .when(userStorage.findById(1L)).thenReturn(Optional.of(user));
        Assertions.assertEquals(userServiceImp.get(1L).getId(), 1L);
    }
}
