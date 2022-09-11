package ru.practicum.shareit.user.storage;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserStorageTest {

    @Autowired
    UserStorage userStorage;
    @Autowired
    TestEntityManager entityManager;


    @Test
    public void whenSaveAndGetAndDelete_thenPositive() {

        User user1 = new User();
        user1.setName("rrr");
        user1.setEmail("ttt@mail.ru");
        userStorage.save(user1);
        User user = userStorage.findById(1L).get();
        assertEquals(user1.getName(), user.getName());
        userStorage.deleteById(1L);
        assertEquals(Optional.empty(), userStorage.findById(1L));
    }

}