package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.List;
@Component
public interface UserStorage {
    User add(User user);
    User get(Long id);
    User update(User user);
    List<User> getAll();
    User delete(Long id);
    Long getNextId();

}
