package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.UserExceptions.UserNotFound;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/*
@Component
public class InMemoryUserStorage implements UserStorage {

    private final List<User> storage = new ArrayList<>();
    private Long id = 0L;

    @Override
    public User add(User user) {
        storage.add(user);
        return user;
    }

    @Override
    public User get(Long id) {

        return storage.stream().filter(o -> Objects.equals(o.getId(), id)).findFirst().orElse(null);
    }

    @Override
    public User update(User user) {

        return null;
    }

    @Override
    public List<User> getAll() {

        return storage;
    }

    @Override
    public User delete(Long id) {
        User deleteUser = storage.stream().filter(o -> Objects.equals(o.getId(), id)).findFirst().orElse(null);
        if (deleteUser != null) {
            storage.removeIf(o -> Objects.equals(o.getId(), id));
        } else {
            throw new UserNotFound("user with id " + id + " not found");
        }
        return deleteUser;
    }

    @Override
    public Long getNextId() {

        return ++id;
    }
}*/
