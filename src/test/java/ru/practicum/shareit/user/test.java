package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.util.Assert;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.userSevice.UserServiceImp;

import java.util.Optional;

public class test {
    @Test
    public void test1(){

        User user = new User();
        user.setId(1l);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        UserServiceImp userServiceImp = new UserServiceImp(userStorage);
        Mockito
                .when(userStorage.findById(1l)).thenReturn(Optional.of(user));
        Assertions.assertEquals(userServiceImp.get(1l).getId(),1l);
    }
}
