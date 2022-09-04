package ru.practicum.shareit.requests.storage;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RequestStorageTest {

    @Autowired
    RequestStorage requestStorage;
    @Autowired
    UserStorage userStorage;
    @Autowired
    TestEntityManager entityManager;


    @Test
    public void whenFindByRequestor_idOrderByCreated_thenPositive() {

        User user = new User();
        user.setName("tt");
        user.setEmail("rrr@mail.ru");
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(user);
        itemRequest.setDescription("ttt");
        itemRequest.setCreated(LocalDateTime.now());
        userStorage.save(user);
        requestStorage.saveAndFlush(itemRequest);
        ItemRequest itemRequest1 = requestStorage.findByRequestor_idOrderByCreated(1L).get();
        assertEquals(itemRequest1.getRequestor().getName(), itemRequest.getRequestor().getName());

    }

}